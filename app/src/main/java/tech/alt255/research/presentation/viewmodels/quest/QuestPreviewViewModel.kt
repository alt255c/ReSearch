package tech.alt255.research.presentation.viewmodels.quest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.alt255.research.data.model.quest.QuestPreview
import tech.alt255.research.domain.repository.QuestRepository
import tech.alt255.research.domain.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class QuestPreviewViewModel @Inject constructor(
    private val questRepository: QuestRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _questPreview = MutableStateFlow<QuestPreview?>(null)
    val questPreview: StateFlow<QuestPreview?> = _questPreview.asStateFlow()

    private val _currentProgress = MutableStateFlow(0)
    val currentProgress: StateFlow<Int> = _currentProgress.asStateFlow()

    private val _totalSteps = MutableStateFlow(0)
    val totalSteps: StateFlow<Int> = _totalSteps.asStateFlow()

    private val _isProgressLoading = MutableStateFlow(false)
    val isProgressLoading: StateFlow<Boolean> = _isProgressLoading.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _acceptQuestResult = MutableStateFlow<AcceptQuestResult?>(null)
    val acceptQuestResult: StateFlow<AcceptQuestResult?> = _acceptQuestResult.asStateFlow()

    fun loadQuestPreview(userId: Int, token: String, questId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val response = questRepository.getQuestPreview(userId, token, questId)

                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.data
                    val preview = data?.preview ?: data?.let {
                        if (it.id != null) {
                            QuestPreview(
                                id = it.id,
                                title = it.title ?: "",
                                description = it.description ?: "",
                                questType = it.questType ?: "",
                                status = it.status ?: "",
                                endDate = it.endDate,
                                isAccepted = it.isAccepted ?: false,
                                userStatus = it.userStatus,
                                rewardStars = it.rewardStars ?: 0,
                                rewardCat = it.rewardCat,
                                districtName = it.districtName,
                                stepsCount = it.stepsCount ?: 0
                            )
                        } else null
                    }

                    if (preview != null) {
                        _questPreview.value = preview
                        _totalSteps.value = preview.stepsCount
                        loadQuestProgress(userId, token, questId)
                    } else {
                        _errorMessage.value = "Не удалось получить информацию о квесте"
                        _isProgressLoading.value = false
                    }
                } else {
                    _errorMessage.value = response.body()?.message ?: "Ошибка загрузки квеста"
                    _isProgressLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка сети: ${e.message}"
                _isProgressLoading.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadQuestProgress(userId: Int, token: String, questId: Int) {
        viewModelScope.launch {
            try {
                _isProgressLoading.value = true
                val response = userRepository.getUserQuests(userId, token, page = 1, limit = 100)

                if (response.isSuccessful && response.body()?.success == true) {
                    val userQuests = response.body()?.data?.quests ?: emptyList()
                    val found = userQuests.find { it.id == questId }
                    _currentProgress.value = found?.progress ?: 0
                } else {
                    _currentProgress.value = 0
                }
            } catch (e: Exception) {
                _currentProgress.value = 0
            } finally {
                _isProgressLoading.value = false
            }
        }
    }

    fun acceptQuest(userId: Int, token: String, questId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                _acceptQuestResult.value = null

                val response = questRepository.acceptQuest(userId, token, questId)

                if (response.isSuccessful && response.body()?.success == true) {
                    _acceptQuestResult.value = AcceptQuestResult.Success(
                        questId = response.body()?.data?.questId ?: questId,
                        message = response.body()?.message ?: "Квест принят"
                    )
                    loadQuestPreview(userId, token, questId)
                } else {
                    _errorMessage.value = response.body()?.message ?: "Ошибка принятия квеста"
                    _acceptQuestResult.value = AcceptQuestResult.Error(
                        message = response.body()?.message ?: "Ошибка принятия квеста"
                    )
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка сети: ${e.message}"
                _acceptQuestResult.value = AcceptQuestResult.Error(
                    message = "Ошибка сети: ${e.message}"
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearAcceptResult() {
        _acceptQuestResult.value = null
    }
}

sealed class AcceptQuestResult {
    data class Success(val questId: Int, val message: String) : AcceptQuestResult()
    data class Error(val message: String) : AcceptQuestResult()
}