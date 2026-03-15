package com.example.androidstudio.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.*

@Serializable
data class CVResponse(
    val id: Int? = null,
    val user_id: Int? = null,
    val filename: String,
    val file_url: String,
    val created_at: String? = null
)

object NetworkConfig {
    const val BASE_URL = "http://192.168.0.104:8000"
}

object TokenHolder {
    var token: String? = null
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
    val filename: String,
    val file_url: String
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
}

object RetrofitClient {
    private val json = Json { ignoreUnknownKeys = true }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
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
