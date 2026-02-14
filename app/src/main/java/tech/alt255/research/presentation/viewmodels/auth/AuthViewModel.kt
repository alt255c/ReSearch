package tech.alt255.research.presentation.viewmodels.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import tech.alt255.research.data.local.SecurePrefs
import tech.alt255.research.domain.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val securePrefs: SecurePrefs
) : ViewModel() {

    private val stateMutex = Mutex()

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _codeVerificationState = MutableStateFlow(CodeVerificationState())
    val codeVerificationState: StateFlow<CodeVerificationState> = _codeVerificationState

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _confirmPasswordError = MutableStateFlow<String?>(null)
    val confirmPasswordError: StateFlow<String?> = _confirmPasswordError

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _codeError = MutableStateFlow<String?>(null)
    val codeError: StateFlow<String?> = _codeError.asStateFlow()

    private var isAutoVerifying = false

    sealed class NavigationEvent {
        data class ToCodeVerificationScreen(val email: String, val type: CodeVerificationType) : NavigationEvent()
        data class ToHomeScreen(val userId: Int, val email: String) : NavigationEvent()
        object ToSignInScreen : NavigationEvent()
        object ToSignUpScreen : NavigationEvent()
    }

    sealed class CodeVerificationType {
        object Registration : CodeVerificationType()
        object PasswordReset : CodeVerificationType()
    }

    data class CodeVerificationState(
        val email: String = "",
        val type: CodeVerificationType = CodeVerificationType.Registration,
        val verifiedDigits: List<Int> = emptyList(),
        val isVerifying: Boolean = false
    )

    fun setEmail(email: String) {
        _email.value = email
    }

    private fun resetErrors() {
        _emailError.value = null
        _passwordError.value = null
        _confirmPasswordError.value = null
        _loginError.value = null
        _codeError.value = null
    }

    fun registerStep1(email: String, password: String) {
        viewModelScope.launch {
            try {
                stateMutex.withLock {
                    log("registerStep1: starting with email=$email")
                    resetErrors()

                    if (_isLoading.value) {
                        log("registerStep1: already loading, skipping")
                        return@withLock
                    }

                    _isLoading.value = true
                    _uiState.value = AuthUiState.Loading
                }

                log("registerStep1: calling repository")
                val response = authRepository.registerStep1(email, password)

                stateMutex.withLock {
                    log("registerStep1: response received, isSuccessful=${response.isSuccessful}, success=${response.body()?.success}")

                    if (response.isSuccessful && response.body()?.success == true) {
                        _email.value = email
                        _uiState.value = AuthUiState.NeedVerification(email)
                        _navigationEvents.emit(NavigationEvent.ToCodeVerificationScreen(email, CodeVerificationType.Registration))
                        log("registerStep1: set state to NeedVerification with email=$email")
                    } else {
                        val errorMsg = response.body()?.message ?: "Ошибка регистрации"

                        if (errorMsg.contains("уже существует", ignoreCase = true) ||
                            errorMsg.contains("already exists", ignoreCase = true)) {
                            _emailError.value = "Почта уже зарегистрирована"
                        } else {
                            _uiState.value = AuthUiState.Error(errorMsg)
                        }
                        log("registerStep1: error - $errorMsg")
                    }
                }
            } catch (e: Exception) {
                stateMutex.withLock {
                    val error = when {
                        e.message?.contains("Expected BEGIN_OBJECT") == true -> {
                            log("registerStep1: JSON parsing error - server returned array instead of object")
                            _email.value = email
                            AuthUiState.NeedVerification(email)
                        }
                        else -> {
                            val errorMsg = "Ошибка сети: ${e.message}"
                            log("registerStep1: exception - $errorMsg")
                            AuthUiState.Error(errorMsg)
                        }
                    }
                    _uiState.value = error
                }
            } finally {
                stateMutex.withLock {
                    _isLoading.value = false
                    log("registerStep1: finished, isLoading=${_isLoading.value}")
                }
            }
        }
    }

    fun registerStep2(email: String, code: String) {
        viewModelScope.launch {
            try {
                stateMutex.withLock {
                    log("registerStep2: starting with email=$email, code=$code")
                    resetErrors()

                    if (_isLoading.value || isAutoVerifying) {
                        log("registerStep2: already loading or auto-verifying, skipping")
                        return@withLock
                    }

                    isAutoVerifying = true
                    _isLoading.value = true
                    _uiState.value = AuthUiState.Loading
                    _codeVerificationState.value = CodeVerificationState(
                        email = email,
                        type = CodeVerificationType.Registration,
                        isVerifying = true
                    )
                }

                for (i in 1..5) {
                    delay(200)
                    stateMutex.withLock {
                        _codeVerificationState.value = _codeVerificationState.value.copy(
                            verifiedDigits = (1..i).toList()
                        )
                    }
                }

                log("registerStep2: calling repository")
                val response = authRepository.registerStep2(email, code)

                stateMutex.withLock {
                    log("registerStep2: response received, isSuccessful=${response.isSuccessful}, success=${response.body()?.success}")

                    if (response.isSuccessful && response.body()?.success == true) {
                        try {
                            val userId = response.body()?.data?.userId ?: 0
                            val userEmail = response.body()?.data?.email ?: email
                            val token = response.body()?.data?.token

                            if (token != null) {
                                securePrefs.saveToken(token)
                                securePrefs.saveUserId(userId)
                            }

                            delay(1000)

                            _uiState.value = AuthUiState.Success("Регистрация завершена")
                            _navigationEvents.emit(NavigationEvent.ToHomeScreen(userId, userEmail))
                            log("registerStep2: registration completed successfully, userId=$userId")
                        } catch (e: Exception) {
                            log("registerStep2: Could not parse response, using defaults")
                            delay(1000)
                            _uiState.value = AuthUiState.Success("Регистрация завершена")
                            _navigationEvents.emit(NavigationEvent.ToHomeScreen(0, email))
                        }
                    } else {
                        val errorMsg = response.body()?.message ?: "Ошибка подтверждения"
                        _codeError.value = errorMsg
                        _uiState.value = AuthUiState.Error(errorMsg)
                        log("registerStep2: error - $errorMsg")
                    }
                }
            } catch (e: Exception) {
                stateMutex.withLock {
                    val error = when {
                        e.message?.contains("Expected BEGIN_OBJECT") == true -> {
                            log("registerStep2: JSON parsing error - server returned array instead of object")

                            delay(1000)

                            AuthUiState.Success("Регистрация завершена")
                        }
                        else -> {
                            val errorMsg = "Ошибка сети: ${e.message}"
                            log("registerStep2: exception - $errorMsg")
                            AuthUiState.Error(errorMsg)
                        }
                    }
                    _uiState.value = error
                }
            } finally {
                stateMutex.withLock {
                    _isLoading.value = false
                    isAutoVerifying = false
                    _codeVerificationState.value = CodeVerificationState(
                        email = _codeVerificationState.value.email,
                        type = _codeVerificationState.value.type,
                        verifiedDigits = emptyList(),
                        isVerifying = false
                    )
                    log("registerStep2: finished, isLoading=${_isLoading.value}")
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                stateMutex.withLock {
                    log("login: starting with email=$email")
                    resetErrors()

                    if (email.isBlank()) {
                        _emailError.value = "Введите почту"
                        return@withLock
                    }
                    if (password.isBlank()) {
                        _passwordError.value = "Введите пароль"
                        return@withLock
                    }

                    if (_isLoading.value) {
                        log("login: already loading, skipping")
                        return@withLock
                    }

                    _isLoading.value = true
                    _uiState.value = AuthUiState.Loading
                }

                log("login: calling repository")
                val response = authRepository.login(email, password)

                stateMutex.withLock {
                    log("login: response received, isSuccessful=${response.isSuccessful}, success=${response.body()?.success}")

                    if (response.isSuccessful && response.body()?.success == true) {
                        try {
                            val userId = response.body()?.data?.userId ?: 0
                            val token = response.body()?.data?.token

                            if (token != null) {
                                securePrefs.saveToken(token)
                                securePrefs.saveUserId(userId)
                            }

                            _uiState.value = AuthUiState.LoginSuccess(userId, email)
                            _navigationEvents.emit(NavigationEvent.ToHomeScreen(userId, email))
                        } catch (e: Exception) {
                            log("login: Could not parse userId from response, using default")
                            _uiState.value = AuthUiState.LoginSuccess(0, email)
                            _navigationEvents.emit(NavigationEvent.ToHomeScreen(0, email))
                        }
                    } else {
                        val errorMsg = response.body()?.message ?: "Ошибка входа"
                        if (errorMsg.contains("Неверный email или пароль", ignoreCase = true) ||
                            errorMsg.contains("неверный", ignoreCase = true) ||
                            errorMsg.contains("invalid", ignoreCase = true)) {
                            _loginError.value = "Почта или пароль неверны"
                            _emailError.value = ""
                            _passwordError.value = ""
                        } else {
                            _uiState.value = AuthUiState.Error(errorMsg)
                        }
                        log("login: error - $errorMsg")
                    }
                }
            } catch (e: Exception) {
                stateMutex.withLock {
                    val error = when {
                        e.message?.contains("Expected BEGIN_OBJECT") == true -> {
                            log("login: JSON parsing error - server returned array instead of object")
                            AuthUiState.LoginSuccess(0, email)
                        }
                        else -> {
                            val errorMsg = "Ошибка сети: ${e.message}"
                            log("login: exception - $errorMsg")
                            AuthUiState.Error(errorMsg)
                        }
                    }
                    _uiState.value = error
                }
            } finally {
                stateMutex.withLock {
                    _isLoading.value = false
                    log("login: finished, isLoading=${_isLoading.value}")
                }
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            try {
                stateMutex.withLock {
                    log("forgotPassword: starting with email=$email")
                    resetErrors()

                    if (email.isBlank()) {
                        _emailError.value = "Введите почту для восстановления"
                        return@withLock
                    }

                    if (_isLoading.value) {
                        log("forgotPassword: already loading, skipping")
                        return@withLock
                    }

                    _isLoading.value = true
                    _uiState.value = AuthUiState.Loading
                }

                log("forgotPassword: calling repository")
                val response = authRepository.forgotPassword(email)

                stateMutex.withLock {
                    log("forgotPassword: response received, isSuccessful=${response.isSuccessful}, success=${response.body()?.success}")

                    if (response.isSuccessful && response.body()?.success == true) {
                        _uiState.value = AuthUiState.Success("Код восстановления отправлен")
                        _codeVerificationState.value = CodeVerificationState(
                            email = email,
                            type = CodeVerificationType.PasswordReset
                        )
                        _navigationEvents.emit(NavigationEvent.ToCodeVerificationScreen(email, CodeVerificationType.PasswordReset))
                        log("forgotPassword: success, navigating to code verification, email=$email, type=PasswordReset")
                    } else {
                        val errorMsg = response.body()?.message ?: "Ошибка восстановления"
                        _uiState.value = AuthUiState.Error(errorMsg)
                        log("forgotPassword: error - $errorMsg")
                    }
                }
            } catch (e: Exception) {
                stateMutex.withLock {
                    val error = when {
                        e.message?.contains("Expected BEGIN_OBJECT") == true -> {
                            log("forgotPassword: JSON parsing error - server returned array instead of object")
                            AuthUiState.Success("Код восстановления отправлен")
                        }
                        else -> {
                            val errorMsg = "Ошибка сети: ${e.message}"
                            log("forgotPassword: exception - $errorMsg")
                            AuthUiState.Error(errorMsg)
                        }
                    }
                    _uiState.value = error
                }
            } finally {
                stateMutex.withLock {
                    _isLoading.value = false
                    log("forgotPassword: finished, isLoading=${_isLoading.value}")
                }
            }
        }
    }

    fun resetState() {
        viewModelScope.launch {
            stateMutex.withLock {
                log("resetState: resetting UI state to Idle")
                resetErrors()
                _uiState.value = AuthUiState.Idle
            }
        }
    }

    fun resetCodeError() {
        viewModelScope.launch {
            stateMutex.withLock {
                _codeError.value = null
            }
        }
    }

    private fun log(message: String) {
        println("AuthViewModel: $message")
        android.util.Log.d("AuthViewModel", message)
    }

    fun resetPassword(email: String, code: String) {
        viewModelScope.launch {
            try {
                stateMutex.withLock {
                    log("resetPassword: starting with email=$email, code=$code")
                    resetErrors()

                    if (_isLoading.value || isAutoVerifying) {
                        log("resetPassword: already loading or auto-verifying, skipping")
                        return@withLock
                    }

                    isAutoVerifying = true
                    _isLoading.value = true
                    _uiState.value = AuthUiState.Loading
                    _codeVerificationState.value = CodeVerificationState(
                        email = email,
                        type = CodeVerificationType.PasswordReset,
                        isVerifying = true
                    )
                }

                for (i in 1..5) {
                    delay(200)
                    stateMutex.withLock {
                        _codeVerificationState.value = _codeVerificationState.value.copy(
                            verifiedDigits = (1..i).toList()
                        )
                    }
                }

                log("resetPassword: calling repository")
                val response = authRepository.resetPassword(email, code)

                stateMutex.withLock {
                    log("resetPassword: response received, isSuccessful=${response.isSuccessful}, success=${response.body()?.success}")

                    if (response.isSuccessful && response.body()?.success == true) {
                        delay(1000)

                        _uiState.value = AuthUiState.Success("Новый пароль отправлен на вашу почту")
                        _navigationEvents.emit(NavigationEvent.ToSignInScreen)
                        log("resetPassword: password reset completed successfully")
                    } else {
                        val errorMsg = response.body()?.message ?: "Ошибка сброса пароля"
                        _codeError.value = errorMsg
                        _uiState.value = AuthUiState.Error(errorMsg)
                        log("resetPassword: error - $errorMsg")
                    }
                }
            } catch (e: Exception) {
                stateMutex.withLock {
                    val error = when {
                        e.message?.contains("Expected BEGIN_OBJECT") == true -> {
                            log("resetPassword: JSON parsing error - server returned array instead of object")

                            delay(1000)

                            AuthUiState.Success("Новый пароль отправлен на вашу почту")
                        }
                        else -> {
                            val errorMsg = "Ошибка сети: ${e.message}"
                            log("resetPassword: exception - $errorMsg")
                            AuthUiState.Error(errorMsg)
                        }
                    }
                    _uiState.value = error
                }
            } finally {
                stateMutex.withLock {
                    _isLoading.value = false
                    isAutoVerifying = false
                    _codeVerificationState.value = CodeVerificationState(
                        email = _codeVerificationState.value.email,
                        type = _codeVerificationState.value.type,
                        verifiedDigits = emptyList(),
                        isVerifying = false
                    )
                    log("resetPassword: finished, isLoading=${_isLoading.value}")
                }
            }
        }
    }
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class NeedVerification(val email: String) : AuthUiState()
    data class Success(val message: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
    data class LoginSuccess(val userId: Int, val email: String) : AuthUiState()
}