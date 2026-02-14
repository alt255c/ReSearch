package tech.alt255.research.presentation.viewmodels.quest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.alt255.research.data.model.quest.QuestStep
import tech.alt255.research.data.model.quest.StepReward
import tech.alt255.research.domain.repository.QuestRepository
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class QuestStepViewModel @Inject constructor(
    private val questRepository: QuestRepository
) : ViewModel() {

    private val _questStep = MutableStateFlow<QuestStep?>(null)
    val questStep: StateFlow<QuestStep?> = _questStep.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _submitResult = MutableStateFlow<SubmitResult?>(null)
    val submitResult: StateFlow<SubmitResult?> = _submitResult.asStateFlow()

    fun loadQuestStep(userId: Int, token: String, questId: Int, stepNumber: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                _submitResult.value = null

                val response = questRepository.getQuestStep(userId, token, questId, stepNumber)

                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.data
                    val step = data?.step ?: data?.let {
                        if (it.stepId != null) {
                            QuestStep(
                                stepId = it.stepId,
                                stepNumber = it.stepNumber ?: stepNumber,
                                taskDescription = it.taskDescription ?: "",
                                taskType = it.taskType ?: "",
                                points = it.points ?: 0,
                                currentProgress = it.currentProgress ?: 0,
                                userStatus = it.userStatus ?: ""
                            )
                        } else null
                    }
                    if (step != null) {
                        _questStep.value = step
                    } else {
                        _errorMessage.value = "Не удалось получить информацию о шаге"
                    }
                } else {
                    val errorMsg = response.body()?.message
                        ?: try {
                            response.errorBody()?.string()?.let {
                                JSONObject(it).optString("message", "Ошибка загрузки шага")
                            } ?: "Ошибка загрузки шага"
                        } catch (e: Exception) {
                            "Ошибка загрузки шага"
                        }
                    _errorMessage.value = errorMsg
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка сети: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitStep(userId: Int, token: String, questId: Int, stepNumber: Int, answer: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                _submitResult.value = null

                val response = questRepository.submitQuestStep(userId, token, questId, stepNumber, answer)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        val data = body.data
                        val isFinalStep = data?.isFinalStep == true
                        val reward = data?.reward

                        _submitResult.value = SubmitResult.Success(
                            isFinalStep = isFinalStep,
                            reward = reward,
                            message = body.message ?: "Шаг выполнен"
                        )
                    } else {
                        _submitResult.value = SubmitResult.Error(
                            message = body?.message ?: "Ошибка выполнения шага"
                        )
                    }
                } else {
                    val errorMsg = try {
                        response.errorBody()?.string()?.let {
                            JSONObject(it).optString("message", "Ошибка сервера: ${response.code()}")
                        } ?: "Ошибка сервера: ${response.code()}"
                    } catch (e: Exception) {
                        "Ошибка сервера: ${response.code()}"
                    }
                    _errorMessage.value = errorMsg
                    _submitResult.value = SubmitResult.Error(message = errorMsg)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка сети: ${e.message}"
                _submitResult.value = SubmitResult.Error(message = "Ошибка сети: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSubmitResult() {
        _submitResult.value = null
    }
}

sealed class SubmitResult {
    data class Success(
        val isFinalStep: Boolean,
        val reward: StepReward?,
        val message: String
    ) : SubmitResult()

    data class Error(val message: String) : SubmitResult()
}