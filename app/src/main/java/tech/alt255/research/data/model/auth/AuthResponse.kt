package tech.alt255.research.data.model.auth

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("data") val data: AuthData? = null
)

data class AuthData(
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("token") val token: String? = null
)

data class RegisterRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("action") val action: String = "register_step1"
)

data class VerifyCodeRequest(
    @SerializedName("email") val email: String,
    @SerializedName("code") val code: String,
    @SerializedName("action") val action: String = "register_step2"
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("action") val action: String = "login"
)

data class ForgotPasswordRequest(
    @SerializedName("email") val email: String,
    @SerializedName("action") val action: String = "forgot_password"
)

data class ResetPasswordRequest(
    @SerializedName("email") val email: String,
    @SerializedName("code") val code: String,
    @SerializedName("action") val action: String = "reset_password"
)