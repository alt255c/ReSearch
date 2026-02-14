package tech.alt255.research.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import tech.alt255.research.data.model.auth.*

interface AuthService {
    @POST("auth_service.php")
    suspend fun registerStep1(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth_service.php")
    suspend fun registerStep2(@Body request: VerifyCodeRequest): Response<AuthResponse>

    @POST("auth_service.php")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth_service.php")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<AuthResponse>

    @POST("auth_service.php")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<AuthResponse>
}