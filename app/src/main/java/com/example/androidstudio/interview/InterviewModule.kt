package com.example.androidstudio.interview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.androidstudio.network.InterviewSessionUpdateRequest
import com.example.androidstudio.network.RetrofitClient
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class InterviewModule(
    context: Context,
    private val userId: Int,
    private val sessionId: Int,
    private val onResponseReceived: (String, String) -> Unit, // Trả về Question và Hint
    private val onUserSpeechRecognized: (String) -> Unit,
    private val onListeningStateChanged: (Boolean) -> Unit,
    private val onError: (String) -> Unit
) : TextToSpeech.OnInitListener {

    private val appContext = context.applicationContext
    private val apiKey = "AIzaSyBNEMjxd6_JM-CmJ988yPDLtSEVqry5eJE"
    private val modelName = "gemini-2.5-flash"

    private val generativeModel = GenerativeModel(
        modelName = modelName,
        apiKey = apiKey,
        systemInstruction = content {
            text("Bạn là một người phỏng vấn chuyên nghiệp. " +
                    "Mỗi phản hồi của bạn PHẢI LUÔN là một đối tượng JSON có định dạng chính xác sau: " +
                    "{\"question\": \"nội dung câu hỏi\", \"hint\": \"gợi ý trả lời ngắn gọn\"}. " +
                    "Không bao gồm bất kỳ văn bản nào khác ngoài JSON. " +
                    "Hãy đặt từng câu hỏi một. Sau khi ứng viên trả lời, hãy nhận xét ngắn gọn và đặt câu hỏi tiếp theo.")
        }
    )

    private val chat = generativeModel.startChat()
    private val scope = CoroutineScope(Dispatchers.Main)
    
    // Biến lưu trữ lịch sử cuộc hội thoại
    private val conversationHistory = mutableListOf<JSONObject>()

    private val speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(appContext)
    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var pendingText: String? = null

    private val handler = Handler(Looper.getMainLooper())
    private val silenceRunnable = Runnable {
        stopListening()
    }

    private val recognizerIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
    }

    init {
        tts = TextToSpeech(appContext, this)
        setupSpeechRecognizer()
    }

    private fun resetSilenceTimer() {
        handler.removeCallbacks(silenceRunnable)
        handler.postDelayed(silenceRunnable, 2000)
    }

    private fun cancelSilenceTimer() {
        handler.removeCallbacks(silenceRunnable)
    }

    fun connect() {
        scope.launch {
            try {
                val response = chat.sendMessage("Bắt đầu buổi phỏng vấn. Hãy chào ứng viên và đặt câu hỏi đầu tiên.")
                parseAndEmitResponse(response.text)
            } catch (e: Exception) {
                onError("Lỗi kết nối Gemini: ${e.message}")
            }
        }
    }

    private fun saveToHistory(role: String, content: String) {
        try {
            val entry = JSONObject()
            entry.put("role", role)
            entry.put("content", content)
            conversationHistory.add(entry)
        } catch (e: Exception) {
            Log.e("InterviewModule", "Error saving to history: ${e.message}")
        }
    }

    private fun sendDebugInfo() {
        if (conversationHistory.isEmpty()) return
        /*
        val historyJson = conversationHistory.toString()
        scope.launch(Dispatchers.IO) {
            try {
                val encodedContent = URLEncoder.encode(historyJson, "UTF-8")
                RetrofitClient.apiService.sendDebugLog(encodedContent)
                Log.d("InterviewModule", "Debug info sent successfully")
            } catch (e: Exception) {
                Log.e("InterviewModule", "Failed to send debug log: ${e.message}")
            }
        }
                
         */
    }

    private fun analyzeAndSaveFeedback() {
        if (conversationHistory.isEmpty()) return

        scope.launch(Dispatchers.IO) {
            try {
                val historyText = conversationHistory.joinToString("\n") { 
                    "${it.getString("role")}: ${it.getString("content")}" 
                }

                val prompt = """
                    Bạn là một chuyên gia tuyển dụng. Hãy đánh giá cuộc phỏng vấn sau đây và trả về một đối tượng JSON duy nhất.
                    Lịch sử cuộc phỏng vấn:
                    $historyText
                    
                    Định dạng JSON yêu cầu:
                    {
                      "general_feedback": "nhận xét chung dạng văn bản",
                      "scores": {
                        "problem_solving": 0-10,
                        "knowledge": 0-10,
                        "communication": 0-10,
                        "soft_skills": 0-10
                      },
                      "strengths": ["điểm mạnh 1", "điểm mạnh 2"],
                      "improvements": ["cần cải thiện 1", "cần cải thiện 2"]
                    }
                    Chỉ trả về JSON, không thêm bất kỳ văn bản nào khác.
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                val feedbackJsonStr = response.text?.let { text ->
                    val start = text.indexOf("{")
                    val end = text.lastIndexOf("}") + 1
                    if (start != -1 && end != -1) text.substring(start, end) else null
                }

                if (feedbackJsonStr != null) {
                    val feedbackObj = JSONObject(feedbackJsonStr)
                    val scores = feedbackObj.getJSONObject("scores")
                    val totalScore = scores.optDouble("problem_solving", 0.0) +
                                   scores.optDouble("knowledge", 0.0) +
                                   scores.optDouble("communication", 0.0) +
                                   scores.optDouble("soft_skills", 0.0)
                    val avgScore = totalScore / 4.0

                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                    val currentTime = sdf.format(Date())

                    val updateRequest = InterviewSessionUpdateRequest(
                        status = "completed",
                        feedbackJson = feedbackJsonStr,
                        endTime = currentTime,
                        score = avgScore
                    )
                    RetrofitClient.apiService.updateInterviewSession(sessionId, updateRequest)
                    Log.d("InterviewModule", "Feedback and end time saved successfully. Score: $avgScore")
                }
            } catch (e: Exception) {
                Log.e("InterviewModule", "Error during analysis: ${e.message}")
            }
        }
    }

    private fun parseAndEmitResponse(text: String?) {
        try {
            if (text == null) return
            
            // Tìm JSON trong chuỗi trả về
            val jsonStart = text.indexOf("{")
            val jsonEnd = text.lastIndexOf("}") + 1
            if (jsonStart != -1 && jsonEnd != -1) {
                val jsonStr = text.substring(jsonStart, jsonEnd)
                val jsonObject = JSONObject(jsonStr)
                val question = jsonObject.optString("question", text)
                val hint = jsonObject.optString("hint", "")
                
                saveToHistory("interviewer", question)
                onResponseReceived(question, hint)
                speak(question)
            } else {
                saveToHistory("interviewer", text)
                onResponseReceived(text, "")
                speak(text)
            }
        } catch (e: Exception) {
            saveToHistory("interviewer", text ?: "")
            onResponseReceived(text ?: "", "")
            speak(text ?: "")
        }
    }

    private fun setupSpeechRecognizer() {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { 
                onListeningStateChanged(true)
                resetSilenceTimer()
            }
            override fun onBeginningOfSpeech() { resetSilenceTimer() }
            override fun onRmsChanged(rmsdB: Float) { if (rmsdB > 2.0f) resetSilenceTimer() }
            override fun onPartialResults(partialResults: Bundle?) {
                val text = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)
                if (!text.isNullOrEmpty()) {
                    onUserSpeechRecognized(text)
                    resetSilenceTimer()
                }
            }
            override fun onResults(results: Bundle?) {
                cancelSilenceTimer()
                onListeningStateChanged(false)
                val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)
                if (!text.isNullOrEmpty()) {
                    onUserSpeechRecognized(text)
                    processUserAnswer(text)
                }
            }
            override fun onError(error: Int) {
                cancelSilenceTimer()
                onListeningStateChanged(false)
            }
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { cancelSilenceTimer() }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun startListening() {
        tts?.stop()
        speechRecognizer.startListening(recognizerIntent)
    }

    fun stopListening() {
        cancelSilenceTimer()
        speechRecognizer.stopListening()
        onListeningStateChanged(false)
    }

    private fun processUserAnswer(answer: String) {
        saveToHistory("user", answer)
        scope.launch {
            try {
                val response = chat.sendMessage(answer)
                parseAndEmitResponse(response.text)
            } catch (e: Exception) {
                onError("Lỗi AI: ${e.message}")
            }
        }
    }

    fun speak(text: String) {
        if (isTtsReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "InterviewTTS")
        } else {
            pendingText = text
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.setLanguage(Locale("vi", "VN"))
            isTtsReady = true
            pendingText?.let {
                speak(it)
                pendingText = null
            }
        }
    }

    fun disconnect() {
        sendDebugInfo()
        analyzeAndSaveFeedback() // Phân tích và lưu feedback khi kết thúc
        cancelSilenceTimer()
        speechRecognizer.destroy()
        tts?.stop()
        tts?.shutdown()
    }
}
