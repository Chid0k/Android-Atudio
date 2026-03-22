package com.example.androidstudio.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.*
import java.util.concurrent.TimeUnit

@Serializable
data class CVResponse(
    val id: Int? = null,
    val user_id: Int? = null,
    val filename: String? = null,
    val file_url: String? = null,
    val cv_path: String? = null,
    val created_at: String? = null
)

@Serializable
data class KnowledgeArticle(
    @SerialName("Header") val title: String,
    @SerialName("Content") val content: String,
    val description: String = "",
    val id: Int? = null,
    val category: String? = null,
    val created_at: String? = null
)

@Serializable
data class ArticlesResponse(
    val user_id: Int,
    val articles_path: String? = null,
    val articles: List<KnowledgeArticle>
)

@Serializable
data class ArticleDetailResponse(
    val user_id: Int,
    val article_index: Int,
    val article: KnowledgeArticle
)

object NetworkConfig {
    const val BASE_URL = "http://192.168.0.104:8000"
}

object TokenHolder {
    var token: String? = null
}

object SessionManager {
    var sessionId: Int? = null
}

object SessionHistoryManager {
    var sessions: List<InterviewSessionResponse> = emptyList()
}

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val user_id: Int,
    val access_token: String? = null,
    val token_type: String? = null
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    @SerialName("display_name") val displayName: String
)

@Serializable
data class RegisterResponse(
    val id: Int? = null,
    @SerialName("user_id") val userId: Int? = null,
    val email: String? = null,
    @SerialName("password_hash") val passwordHash: String? = null,
    @SerialName("google_id") val googleId: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class UserProfile(
    val id: Int? = null,
    val email: String? = null,
    val full_name: String? = null,
    val major: String? = null,
    val experience: String? = null,
    val skills: String? = null,
    val cv_url: String? = null,
    val cv_filename: String? = null
)

@Serializable
data class ProfileUpdateRequest(
    val user_id: Int,
    val full_name: String,
    val major: String,
    val experience: String,
    val skills: String
)

@Serializable
data class UploadCVResponse(
    val filename: String? = null,
    val file_url: String? = null,
    val cv_path: String? = null
)

@Serializable
data class InterviewSessionRequest(
    @SerialName("user_id") val userId: Int,
    val title: String,
    @SerialName("interview_type") val interviewType: String,
    val difficulty: String,
    val mode: String,
    @SerialName("duration_minutes") val durationMinutes: Int,
    @SerialName("actual_duration") val actualDuration: Int? = 0,
    @SerialName("start_time") val startTime: String? = null,
    @SerialName("end_time") val endTime: String? = null,
    val score: Double? = 0.0,
    val status: String = "pending",
    @SerialName("config_json") val configJson: String? = null,
    @SerialName("ai_questions_json") val aiQuestionsJson: String? = null,
    @SerialName("feedback_json") val feedbackJson: String? = null,
    @SerialName("is_favorite") val isFavorite: Int? = 0
)

@Serializable
data class InterviewSessionResponse(
    @SerialName("session_id") val sessionId: Int,
    @SerialName("user_id") val userId: Int,
    val title: String,
    @SerialName("interview_type") val interviewType: String,
    val difficulty: String,
    val mode: String,
    @SerialName("duration_minutes") val durationMinutes: Int,
    @SerialName("actual_duration") val actualDuration: Int? = null,
    @SerialName("start_time") val startTime: String? = null,
    @SerialName("end_time") val endTime: String? = null,
    val score: Double? = null,
    val status: String,
    @SerialName("config_json") val configJson: String? = null,
    @SerialName("ai_questions_json") val aiQuestionsJson: String? = null,
    @SerialName("feedback_json") val feedbackJson: String? = null,
    @SerialName("is_favorite") val isFavorite: Int? = null
)

@Serializable
data class FeedbackData(
    @SerialName("general_feedback") val generalFeedback: String,
    val scores: Map<String, Int>,
    val strengths: List<String>,
    val improvements: List<String>
)

@Serializable
data class InterviewSessionUpdateRequest(
    @SerialName("actual_duration") val actualDuration: Int? = null,
    @SerialName("start_time") val startTime: String? = null,
    @SerialName("end_time") val endTime: String? = null,
    val score: Double? = null,
    val status: String? = null,
    @SerialName("config_json") val configJson: String? = null,
    @SerialName("feedback_json") val feedbackJson: String? = null,
    @SerialName("is_favorite") val isFavorite: Int? = null
)

interface ApiService {
    @POST("/api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("/api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @GET("/api/v1/profiles/{user_id}")
    suspend fun getUserProfile(@Path("user_id") userId: Int): UserProfile

    @POST("/api/v1/profiles")
    suspend fun createProfile(@Body request: ProfileUpdateRequest): UserProfile

    @PATCH("/api/v1/profiles/{user_id}")
    suspend fun updateProfile(@Path("user_id") userId: Int, @Body request: ProfileUpdateRequest): UserProfile

    @Multipart
    @POST("/api/v1/users/{user_id}/cv")
    suspend fun uploadCV(
        @Path("user_id") userId: Int,
        @Part file: MultipartBody.Part
    ): UploadCVResponse

    @GET("/api/v1/users/{user_id}/cv")
    suspend fun getCVList(@Path("user_id") userId: Int): List<CVResponse>

    @GET("/api/v1/users/{user_id}/articles")
    suspend fun getArticles(@Path("user_id") userId: Int): ArticlesResponse

    @GET("/api/v1/users/{user_id}/articles/{article_index}")
    suspend fun getArticleDetail(
        @Path("user_id") userId: Int,
        @Path("article_index") articleIndex: Int
    ): ArticleDetailResponse

    @POST("/api/v1/interview-sessions")
    suspend fun createInterviewSession(@Body request: InterviewSessionRequest): InterviewSessionResponse

    @PATCH("/api/v1/interview-sessions/{session_id}")
    suspend fun updateInterviewSession(
        @Path("session_id") sessionId: Int,
        @Body request: InterviewSessionUpdateRequest
    ): InterviewSessionResponse

    @GET("/api/v1/interview-sessions/{session_id}")
    suspend fun getInterviewSession(@Path("session_id") sessionId: Int): InterviewSessionResponse

    @GET("/api/v1/users/{user_id}/interview-sessions")
    suspend fun getInterviewSessions(@Path("user_id") userId: Int): List<InterviewSessionResponse>

    @GET("/api/v1/text/{user_id}/{session_id}/{text}")
    suspend fun sendSTTText(
        @Path("user_id") userId: Int,
        @Path("session_id") sessionId: Int,
        @Path("text") text: String
    ): ResponseBody

    @GET("/api/v1/debug/{content}")
    suspend fun sendDebugLog(@Path("content") content: String): ResponseBody
}

object RetrofitClient {
    val json = Json {
        ignoreUnknownKeys = true 
        coerceInputValues = true
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(logging)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
            TokenHolder.token?.let {
                request.addHeader("Authorization", "Bearer $it")
            }
            chain.proceed(request.build())
        }
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
    }
}
