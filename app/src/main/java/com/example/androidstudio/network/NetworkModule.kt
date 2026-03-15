package com.example.androidstudio.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

object NetworkConfig {
    const val BASE_URL = "http://192.168.0.104:8000"
}

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val user_id: Int
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterResponse(
    val id: Int,
    val email: String,
    val password_hash: String? = null,
    val google_id: String? = null,
    val created_at: String? = null
)

@Serializable
data class UserProfile(
    val id: Int,
    val email: String,
    val full_name: String? = null,
    val major: String? = null,
    val experience: String? = null,
    val skills: String? = null
)

@Serializable
data class ProfileUpdateRequest(
    val user_id: Int,
    val full_name: String,
    val major: String,
    val experience: String,
    val skills: String
)

interface ApiService {
    @POST("/api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("/api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @GET("/api/v1/profiles/{user_id}")
    suspend fun getUserProfile(@Path("user_id") userId: Int): UserProfile

    @POST("/api/v1/profiles")
    suspend fun updateProfile(@Body request: ProfileUpdateRequest): UserProfile
}

object RetrofitClient {
    private val json = Json { ignoreUnknownKeys = true }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
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
