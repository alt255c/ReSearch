package tech.alt255.research.domain.repository

import retrofit2.Response
import tech.alt255.research.data.local.SecurePrefs
import tech.alt255.research.data.model.auth.*
import tech.alt255.research.data.remote.AuthService
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val securePrefs: SecurePrefs
) {

    suspend fun registerStep1(email: String, password: String): Response<AuthResponse> {
        return authService.registerStep1(RegisterRequest(email, password))
    }

    suspend fun registerStep2(email: String, code: String): Response<AuthResponse> {
        return authService.registerStep2(VerifyCodeRequest(email, code))
    }

    suspend fun login(email: String, password: String): Response<AuthResponse> {
        return authService.login(LoginRequest(email, password))
    }

    suspend fun forgotPassword(email: String): Response<AuthResponse> {
        return authService.forgotPassword(ForgotPasswordRequest(email))
    }

    suspend fun resetPassword(email: String, code: String): Response<AuthResponse> {
        return authService.resetPassword(ResetPasswordRequest(email, code))
    }

    fun getSavedToken(): String? {
        return securePrefs.getToken()
    }

    fun logout() {
        securePrefs.logout()
    }
}