package tech.alt255.research.presentation.viewmodels.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.alt255.research.data.model.quest.AvailableQuest
import tech.alt255.research.domain.repository.QuestRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val questRepository: QuestRepository
) : ViewModel() {

    private val _availableQuests = MutableStateFlow<List<AvailableQuest>>(emptyList())
    val availableQuests: StateFlow<List<AvailableQuest>> = _availableQuests.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _paginationState = MutableStateFlow<PaginationState>(PaginationState.Idle)
    val paginationState: StateFlow<PaginationState> = _paginationState.asStateFlow()

    private var currentPage = 1
    private var hasMore = true
    private var isLoadingMore = false

    fun loadAvailableQuests(userId: Int, token: String, refresh: Boolean = false) {
        if (isLoadingMore) return

        viewModelScope.launch {
            try {
                if (refresh) {
                    currentPage = 1
                    _isLoading.value = true
                } else {
                    isLoadingMore = true
                    _paginationState.value = PaginationState.Loading
                }

                val response = questRepository.getAvailableQuests(userId, token, currentPage)

                if (response.isSuccessful && response.body()?.success == true) {
                    val newQuests = response.body()?.data?.quests ?: emptyList()
                    val total = response.body()?.data?.total ?: 0
                    val page = response.body()?.data?.page ?: 1
                    val limit = response.body()?.data?.limit ?: 20

                    if (refresh) {
                        _availableQuests.value = newQuests
                    } else {
                        _availableQuests.value = _availableQuests.value + newQuests
                    }

                    currentPage = page + 1
                    hasMore = response.body()?.data?.hasMore == true &&
                            ((page) * limit) < total

                    _errorMessage.value = null
                } else {
                    _errorMessage.value = response.body()?.message ?: "Ошибка загрузки квестов"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка сети: ${e.message}"
            } finally {
                _isLoading.value = false
                isLoadingMore = false
                _paginationState.value = PaginationState.Idle
            }
        }
    }

    fun refreshQuests(userId: Int, token: String) {
        loadAvailableQuests(userId, token, refresh = true)
    }

    fun loadMoreQuests(userId: Int, token: String) {
        if (hasMore && !isLoadingMore) {
            loadAvailableQuests(userId, token)
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

sealed class PaginationState {
    object Idle : PaginationState()
    object Loading : PaginationState()
    data class Error(val message: String) : PaginationState()
}