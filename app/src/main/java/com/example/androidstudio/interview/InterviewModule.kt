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
import com.example.androidstudio.network.SessionManager
import com.example.androidstudio.network.UserProfile
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class InterviewModule(
    context: Context,
    private val userId: Int,
    private val sessionId: Int,
    private val onResponseReceived: (String, String) -> Unit,
    private val onUserSpeechRecognized: (String) -> Unit,
    private val onListeningStateChanged: (Boolean) -> Unit,
    private val onError: (String) -> Unit
) : TextToSpeech.OnInitListener {

    private val appContext = context.applicationContext
    private val apiKey = "AIzaSyBNEMjxd6_JM-CmJ988yPDLtSEVqry5eJE"
    private val modelName = "gemini-2.5-flash"

    private var generativeModel: GenerativeModel? = null
    private var chat: Chat? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    
    private val conversationHistory = mutableListOf<JSONObject>()
    private val speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(appContext)
    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var pendingText: String? = null

    private val handler = Handler(Looper.getMainLooper())
    private val silenceRunnable = Runnable { stopListening() }

    private val recognizerIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        val lang = if (SessionManager.language == "Tiếng Anh") "en-US" else "vi-VN"
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang)
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
    }

    init {
        tts = TextToSpeech(appContext, this)
        setupSpeechRecognizer()
    }

    private fun resetSilenceTimer() {
        handler.removeCallbacks(silenceRunnable)
        handler.postDelayed(silenceRunnable, 5000)
    }

    private fun cancelSilenceTimer() {
        handler.removeCallbacks(silenceRunnable)
    }

    fun connect() {
        scope.launch {
            try {
                // 1. Lấy thông tin Profile người dùng
                val profile = try {
                    RetrofitClient.apiService.getUserProfile(userId)
                } catch (e: Exception) {
                    UserProfile(full_name = "Ứng viên") // Fallback nếu lỗi profile
                }
                
                // 2. Xây dựng instruction và khởi tạo Model
                val instruction = buildSystemInstruction(profile)
                generativeModel = GenerativeModel(
                    modelName = modelName,
                    apiKey = apiKey,
                    systemInstruction = content { text(instruction) }
                )
                
                chat = generativeModel?.startChat()

                // 3. Gửi prompt khởi đầu
                val startPrompt = if (SessionManager.language == "Tiếng Anh") {
                    "Begin the interview. Introduce yourself as an AI character named \"AI Interviewer\" conducting the interview today, then greet me and ask me to introduce myself and my experience. Then, based on the information I provide about the position, job description, and my experience, formulate interview questions."
                } else {
                    "Bắt đầu buổi phỏng vấn. Hãy giới thiệu mình là 1 nhân vật AI tên là \"AI Interviewer\" thực hiện phỏng vấn tôi ngày hôm nay, sau đó chào tôi và yêu cầu tôi giới thiệu bản thân và kinh nghiệm. Sau đó hãy dựa vào thông tin của tôi cung cấp về vị trí ứng tuyển, mô tả công việc và kinh nghiệm của tôi để đưa ra các câu hỏi phỏng vấn"
                }
                
                val response = chat?.sendMessage(startPrompt)
                parseAndEmitResponse(response?.text)
            } catch (e: Exception) {
                onError("Lỗi kết nối AI: ${e.message}")
            }
        }
    }

    private fun buildSystemInstruction(profile: UserProfile): String {
        val language = SessionManager.language
        val jobTitle = SessionManager.jobTitle
        val jobDesc = SessionManager.jobDescription
        
        val userMajor = profile.major ?: "N/A"
        val userExp = profile.experience ?: "N/A"
        val userSkills = profile.skills ?: "N/A"
        val userName = profile.full_name ?: "Ứng viên"
        val userDescription = profile.description ?: "N/A"

        return """
            Bạn là một chuyên gia phỏng vấn chuyên nghiệp cho vị trí: $jobTitle.
            Mô tả công việc: $jobDesc
            
            Thông tin ứng viên:
            - Tên: $userName
            - Chuyên ngành: $userMajor
            - Kinh nghiệm: $userExp
            - Kỹ năng: $userSkills
            - Câu hỏi thêm: $userDescription
            
            YÊU CẦU:
            1. Đặt câu hỏi dựa trên sự kết hợp giữa Mô tả công việc và Hồ sơ ứng viên.
            2. Đặt các câu hỏi tình huồng làm việc thực tế xen kẽ các câu hỏi kiến thức 
            3. Kiểm tra các kỹ năng vào tình huống làm việc thực tế.
            4. Ngôn ngữ: $language.
            
            QUY ĐỊNH PHẢN HỒI:
            - PHẢI LUÔN TRẢ VỀ JSON: {"question": "nội dung", "hint": "gợi ý ngắn"}
            - KHÔNG ĐƯỢC có văn bản thừa ngoài JSON.
            - Đặt từng câu hỏi một. Sau khi nghe trả lời, nhận xét ngắn rồi hỏi câu tiếp.
        """.trimIndent()
    }

    private fun saveToHistory(role: String, content: String) {
        try {
            val entry = JSONObject()
            entry.put("role", role)
            entry.put("content", content)
            conversationHistory.add(entry)
        } catch (e: Exception) {
            Log.e("InterviewModule", "Error saving history: ${e.message}")
        }
    }

    fun analyzeAndSaveFeedback() {
        val model = generativeModel ?: return
        if (conversationHistory.isEmpty()) return

        scope.launch(Dispatchers.IO) {
            try {
                val historyText = conversationHistory.joinToString("\n") { 
                    "${it.getString("role")}: ${it.getString("content")}" 
                }

                val prompt = """
                    Đánh giá cuộc phỏng vấn vị trí ${SessionManager.jobTitle}. Trả về JSON:
                    {
                      "general_feedback": "nhận xét bằng ${SessionManager.language}",
                      "scores": {"problem_solving": 0-10, "knowledge": 0-10, "communication": 0-10, "soft_skills": 0-10},
                      "strengths": [], "improvements": []
                    }
                    Lịch sử: $historyText
                """.trimIndent()

                val response = model.generateContent(prompt)
                val feedbackJsonStr = response.text?.let { text ->
                    val start = text.indexOf("{")
                    val end = text.lastIndexOf("}") + 1
                    if (start != -1 && end != -1) text.substring(start, end) else null
                }

                if (feedbackJsonStr != null) {
                    val feedbackObj = JSONObject(feedbackJsonStr)
                    val scores = feedbackObj.getJSONObject("scores")
                    val avgScore = (scores.optDouble("problem_solving") + scores.optDouble("knowledge") + 
                                   scores.optDouble("communication") + scores.optDouble("soft_skills")) / 4.0

                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    sdf.timeZone = TimeZone.getTimeZone("UTC")

                    val updateRequest = InterviewSessionUpdateRequest(
                        status = "completed",
                        feedbackJson = feedbackJsonStr,
                        endTime = sdf.format(Date()),
                        score = avgScore
                    )
                    RetrofitClient.apiService.updateInterviewSession(sessionId, updateRequest)
                }
            } catch (e: Exception) {
                Log.e("InterviewModule", "Analysis error: ${e.message}")
            }
        }
    }

    private fun parseAndEmitResponse(text: String?) {
        try {
            if (text == null) return
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
                val response = chat?.sendMessage(answer)
                parseAndEmitResponse(response?.text)
            } catch (e: Exception) {
                onError("AI Error: ${e.message}")
            }
        }
    }

    fun speak(text: String) {
        if (isTtsReady && tts != null) {
            tts?.setSpeechRate(1.2f)

            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
        } else {
            pendingText = text
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val locale = if (SessionManager.language == "Tiếng Anh") Locale.US else Locale("vi", "VN")
            tts?.setLanguage(locale)
            isTtsReady = true
            pendingText?.let {
                speak(it)
            }
        }
    }

    fun disconnect() {
        tts?.stop()
        tts?.shutdown()
        speechRecognizer.destroy()
        cancelSilenceTimer()
    }
}
