package tech.alt255.research.presentation.viewmodels.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.alt255.research.data.model.user.UserProfile
import tech.alt255.research.domain.repository.UserRepository
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<UserProfile?>(null)
    val profileState: StateFlow<UserProfile?> = _profileState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()

    fun loadProfile(userId: Int, token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = userRepository.getUserProfile(userId, token)
                if (response.isSuccessful && response.body()?.success == true) {
                    _profileState.value = response.body()?.data?.profile
                } else {
                    _errorMessage.value = response.body()?.message ?: "Ошибка загрузки профиля"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка сети: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(
        userId: Int,
        token: String,
        userName: String,
        userNickname: String,
        currentPassword: String?,
        newPassword: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _updateSuccess.value = false
            try {
                val profileResponse = userRepository.updateUserProfile(
                    userId = userId,
                    token = token,
                    userName = userName,
                    userNickname = userNickname
                )

                if (!profileResponse.isSuccessful) {
                    val errorBody = profileResponse.errorBody()?.string()
                    val errorMsg = try {
                        val json = JSONObject(errorBody ?: "")
                        json.optString("message", "Ошибка обновления профиля")
                    } catch (e: Exception) {
                        "Ошибка обновления профиля"
                    }
                    _errorMessage.value = errorMsg
                    return@launch
                }

                val profileBody = profileResponse.body()
                if (profileBody?.success != true) {
                    _errorMessage.value = profileBody?.message ?: "Ошибка обновления профиля"
                    return@launch
                }

                if (!currentPassword.isNullOrBlank() && !newPassword.isNullOrBlank()) {
                    val passwordResponse = userRepository.updatePassword(
                        userId = userId,
                        token = token,
                        currentPassword = currentPassword,
                        newPassword = newPassword
                    )

                    if (!passwordResponse.isSuccessful) {
                        val errorBody = passwordResponse.errorBody()?.string()
                        val errorMsg = try {
                            val json = JSONObject(errorBody ?: "")
                            json.optString("message", "Ошибка обновления пароля")
                        } catch (e: Exception) {
                            "Ошибка обновления пароля"
                        }
                        _errorMessage.value = errorMsg
                        return@launch
                    }

                    val passwordBody = passwordResponse.body()
                    if (passwordBody?.success != true) {
                        _errorMessage.value = passwordBody?.message ?: "Ошибка обновления пароля"
                        return@launch
                    }
                }

                loadProfile(userId, token)
                _updateSuccess.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка сети: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSuccess() {
        _updateSuccess.value = false
    }
}