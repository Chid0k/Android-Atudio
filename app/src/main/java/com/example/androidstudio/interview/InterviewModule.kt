package com.example.androidstudio.interview

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.util.Base64
import android.util.Log
import com.example.androidstudio.network.NetworkConfig
import com.example.androidstudio.network.SessionManager
import com.example.androidstudio.network.TokenHolder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.*
import java.util.concurrent.TimeUnit

class InterviewModule(private val context: Context, private val userId: Int?) {
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private var webSocket: WebSocket? = null
    
    private val _sttText = MutableStateFlow("")
    val sttText: StateFlow<String> = _sttText

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val json = Json { ignoreUnknownKeys = true }

    // Audio recording settings (matching backend _WS_INPUT_MIME_MAP pcm16)
    private val sampleRateInput = 16000
    private val channelConfigInput = AudioFormat.CHANNEL_IN_MONO
    private val audioFormatInput = AudioFormat.ENCODING_PCM_16BIT
    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null

    // Audio playback settings (matching backend _WS_OUTPUT_MIME_MAP pcm16)
    private val sampleRateOutput = 24000
    private val channelConfigOutput = AudioFormat.CHANNEL_OUT_MONO
    private val audioFormatOutput = AudioFormat.ENCODING_PCM_16BIT
    private var audioTrack: AudioTrack? = null

    init {
        setupAudioTrack()
    }

    private fun setupAudioTrack() {
        val bufferSize = AudioTrack.getMinBufferSize(sampleRateOutput, channelConfigOutput, audioFormatOutput)
        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(android.media.AudioAttributes.Builder()
                .setUsage(android.media.AudioAttributes.USAGE_VOICE_COMMUNICATION)
                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SPEECH)
                .build())
            .setAudioFormat(AudioFormat.Builder()
                .setEncoding(audioFormatOutput)
                .setSampleRate(sampleRateOutput)
                .setChannelMask(channelConfigOutput)
                .build())
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
        audioTrack?.play()
    }

    fun startListening() {
        if (_isListening.value) return
        connectWebSocket()
    }

    private fun connectWebSocket() {
        val sessionId = SessionManager.sessionId ?: return
        val token = TokenHolder.token ?: ""
        val uid = userId ?: 0
        
        val wsBaseUrl = NetworkConfig.BASE_URL.replace("http://", "ws://").replace("https://", "wss://")
        val url = "$wsBaseUrl/ws/v1/interview/audio/$uid/$sessionId?token=$token"

        Log.d("InterviewModule", "Connecting to WebSocket: $url")
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("InterviewModule", "WebSocket Open")
                _isListening.value = true
                _sttText.value = "" // Clear text when starting
                startRecording()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                handleMessage(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("InterviewModule", "WebSocket Failure: ${t.message}")
                stopListening()
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("InterviewModule", "WebSocket Closing: $reason")
                stopListening()
            }
        })
    }

    private fun handleMessage(text: String) {
        try {
            val obj = json.decodeFromString<JsonObject>(text)
            val type = obj["type"]?.jsonPrimitive?.content
            when (type) {
                "model_text" -> {
                    val content = obj["text"]?.jsonPrimitive?.content ?: ""
                    // Streaming text: append new content
                    _sttText.value += content
                }
                "model_audio" -> {
                    val data = obj["data"]?.jsonPrimitive?.content
                    if (data != null) {
                        val audioBytes = Base64.decode(data, Base64.DEFAULT)
                        audioTrack?.write(audioBytes, 0, audioBytes.size)
                    }
                }
                "turn_complete" -> {
                    Log.d("InterviewModule", "Turn complete")
                }
                "ready" -> {
                    Log.d("InterviewModule", "Server ready")
                    _sttText.value = ""
                }
                "error" -> {
                    val detail = obj["detail"]?.jsonPrimitive?.content
                    Log.e("InterviewModule", "Server Error: $detail")
                    _sttText.value = "Error: $detail"
                }
            }
        } catch (e: Exception) {
            Log.e("InterviewModule", "Error parsing message: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    private fun startRecording() {
        val bufferSize = AudioRecord.getMinBufferSize(sampleRateInput, channelConfigInput, audioFormatInput)
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRateInput,
            channelConfigInput,
            audioFormatInput,
            bufferSize
        )

        audioRecord?.startRecording()
        recordingJob = scope.launch {
            val buffer = ByteArray(bufferSize)
            while (isActive && _isListening.value) {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: -1
                if (read > 0) {
                    val chunk = if (read == buffer.size) buffer else buffer.copyOfRange(0, read)
                    val base64Data = Base64.encodeToString(chunk, Base64.NO_WRAP)
                    val message = "{\"type\": \"audio_chunk\", \"data\": \"$base64Data\"}"
                    webSocket?.send(message)
                }
            }
        }
    }

    fun stopListening() {
        if (!_isListening.value) return
        
        // Before stopping/committing, we clear text for the next model turn
        _sttText.value = ""

        _isListening.value = false
        recordingJob?.cancel()
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        audioRecord = null
        
        webSocket?.send("{\"type\": \"commit_audio\"}")
        webSocket?.send("{\"type\": \"close\"}")
        webSocket?.close(1000, "User stopped")
        webSocket = null
    }

    fun destroy() {
        stopListening()
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
        scope.cancel()
    }
}
