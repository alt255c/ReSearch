package tech.alt255.research.presentation.viewmodels.rating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.alt255.research.data.model.rating.RatingUser
import tech.alt255.research.domain.repository.RatingRepository
import javax.inject.Inject

@HiltViewModel
class RatingViewModel @Inject constructor(
    private val ratingRepository: RatingRepository
) : ViewModel() {

    private val _ratingUsers = MutableStateFlow<List<RatingUser>>(emptyList())
    val ratingUsers: StateFlow<List<RatingUser>> = _ratingUsers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _currentUserRank = MutableStateFlow<Int?>(null)
    val currentUserRank: StateFlow<Int?> = _currentUserRank.asStateFlow()

    private var currentPage = 1
    private var hasMore = true
    private var isLoadingMore = false

    fun loadRating(userId: Int, token: String, refresh: Boolean = false) {
        if (isLoadingMore) return

        viewModelScope.launch {
            try {
                if (refresh) {
                    currentPage = 1
                    _isLoading.value = true
                } else {
                    isLoadingMore = true
                }

                val response = ratingRepository.getRating(userId, token, currentPage, 20)

                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.data
                    val newUsers = data?.users ?: emptyList()
                    val total = data?.total ?: 0
                    val page = data?.page ?: 1

                    if (refresh) {
                        _ratingUsers.value = newUsers
                        _currentUserRank.value = data?.currentUserRank
                    } else {
                        _ratingUsers.value = _ratingUsers.value + newUsers
                    }

                    currentPage = page + 1
                    hasMore = (data?.hasMore == true)

                    _errorMessage.value = null
                } else {
                    _errorMessage.value = response.body()?.message ?: "Ошибка загрузки рейтинга"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка сети: ${e.message}"
            } finally {
                _isLoading.value = false
                isLoadingMore = false
            }
        }
    }

    fun refreshRating(userId: Int, token: String) {
        loadRating(userId, token, refresh = true)
    }

    fun loadMoreRating(userId: Int, token: String) {
        if (hasMore && !isLoadingMore) {
            loadRating(userId, token)
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}