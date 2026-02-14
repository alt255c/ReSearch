package tech.alt255.research.presentation.viewmodels.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import tech.alt255.research.data.local.LocalUserDataSource
import tech.alt255.research.data.local.entity.*
import tech.alt255.research.data.model.quest.UserQuest
import tech.alt255.research.data.model.user.LoadState
import tech.alt255.research.data.model.user.PaginatedData
import tech.alt255.research.data.model.user.UserData
import tech.alt255.research.domain.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val localDataSource: LocalUserDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserUiState>(UserUiState.InitialLoading)
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData.asStateFlow()

    private val _selectedMenu = MutableStateFlow(MenuMode.QUESTS)
    val selectedMenu: StateFlow<MenuMode> = _selectedMenu.asStateFlow()

    private val _isRefreshingProfile = MutableStateFlow(false)
    val isRefreshingProfile: StateFlow<Boolean> = _isRefreshingProfile.asStateFlow()

    private val _questsData = MutableStateFlow<PaginatedData<UserQuest>>(
        PaginatedData(emptyList(), 0, 0, false)
    )
    val questsData: StateFlow<PaginatedData<UserQuest>> = _questsData.asStateFlow()

    private val _achievementsData = MutableStateFlow<PaginatedData<tech.alt255.research.data.model.user.UserAchievement>>(
        PaginatedData(emptyList(), 0, 0, false)
    )
    val achievementsData: StateFlow<PaginatedData<tech.alt255.research.data.model.user.UserAchievement>> = _achievementsData.asStateFlow()

    private val _catsData = MutableStateFlow<PaginatedData<tech.alt255.research.data.model.user.UserCat>>(
        PaginatedData(emptyList(), 0, 0, false)
    )
    val catsData: StateFlow<PaginatedData<tech.alt255.research.data.model.user.UserCat>> = _catsData.asStateFlow()

    private val _isPullRefreshing = MutableStateFlow(false)
    val isPullRefreshing: StateFlow<Boolean> = _isPullRefreshing.asStateFlow()

    private val _questsLoadState = MutableStateFlow<LoadState>(LoadState.Initial)

    fun loadUserProfile(userId: Int, token: String, silent: Boolean = false) {
        viewModelScope.launch {
            try {
                if (!silent) {
                    _uiState.value = UserUiState.InitialLoading
                } else {
                    _isRefreshingProfile.value = true
                }

                val cachedProfile = localDataSource.observeProfile(userId).firstOrNull()
                cachedProfile?.let {
                    _userData.value = UserData(
                        profile = tech.alt255.research.data.model.user.UserProfile(
                            id = it.userId,
                            email = it.email,
                            userName = it.userName,
                            userNickname = it.userNickname,
                            userPhoto = it.userPhoto,
                            stars = it.stars,
                            level = it.level,
                            nextLevelStars = it.nextLevelStars
                        )
                    )
                    _uiState.value = UserUiState.Success
                }

                val response = userRepository.getUserProfile(userId, token)

                if (response.isSuccessful && response.body()?.success == true) {
                    val profile = response.body()?.data?.profile
                    if (profile != null) {
                        localDataSource.saveProfile(
                            UserProfileEntity(
                                userId = profile.id,
                                email = profile.email,
                                userName = profile.userName,
                                userNickname = profile.userNickname,
                                userPhoto = profile.userPhoto,
                                stars = profile.stars,
                                level = profile.level,
                                nextLevelStars = profile.nextLevelStars
                            )
                        )
                        _userData.value = UserData(profile = profile)
                        _uiState.value = UserUiState.Success
                    } else {
                        if (cachedProfile == null) {
                            _uiState.value = UserUiState.Error("Не удалось загрузить профиль")
                        }
                    }
                } else {
                    val errorMsg = response.body()?.message ?: "Ошибка загрузки профиля"
                    if (cachedProfile == null) {
                        _uiState.value = UserUiState.Error(errorMsg)
                    }
                }
            } catch (e: Exception) {
                if (_userData.value == null) {
                    _uiState.value = UserUiState.Error("Ошибка сети: ${e.message}")
                }
            } finally {
                _isRefreshingProfile.value = false
            }
        }
    }

    fun loadUserAchievements(userId: Int, token: String, page: Int = 1, isRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                if (page == 1 && !isRefresh) {
                    val cached = localDataSource.observeAchievements(userId).firstOrNull() ?: emptyList()
                    if (cached.isNotEmpty()) {
                        _achievementsData.value = PaginatedData(
                            items = cached.map { it.toModel() },
                            total = cached.size,
                            page = 1,
                            hasMore = true,
                            isLoading = false
                        )
                        _uiState.value = UserUiState.Success
                    }
                }

                _achievementsData.value = _achievementsData.value.copy(isLoading = true)

                val response = userRepository.getUserAchievements(userId, token, page, 20)

                if (response.isSuccessful && response.body()?.success == true) {
                    val achievements = response.body()?.data?.achievements ?: emptyList()
                    val total = response.body()?.data?.total ?: 0
                    val currentPage = response.body()?.data?.page ?: 1
                    val hasMore = response.body()?.data?.hasMore ?: false

                    val entities = achievements.map { ach ->
                        UserAchievementEntity(
                            id = ach.id,
                            userId = userId,
                            name = ach.name,
                            description = ach.description,
                            points = ach.points,
                            isCompleted = ach.isCompleted,
                            unlockedAt = ach.unlockedAt,
                            page = currentPage
                        )
                    }
                    localDataSource.saveAchievements(entities, currentPage, clearPrevious = (page == 1 || isRefresh))

                    val currentList = if (page == 1 || isRefresh) {
                        emptyList()
                    } else {
                        _achievementsData.value.items
                    }

                    _achievementsData.value = PaginatedData(
                        items = currentList + achievements,
                        total = total,
                        page = currentPage,
                        hasMore = hasMore,
                        isLoading = false
                    )

                    _uiState.value = UserUiState.Success
                } else {
                    val errorMsg = response.body()?.message ?: "Ошибка загрузки достижений"
                    if (_achievementsData.value.items.isEmpty()) {
                        _uiState.value = UserUiState.Error(errorMsg)
                    }
                    _achievementsData.value = _achievementsData.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                if (_achievementsData.value.items.isEmpty()) {
                    _uiState.value = UserUiState.Error("Ошибка сети: ${e.message}")
                }
                _achievementsData.value = _achievementsData.value.copy(isLoading = false)
            }
        }
    }

    fun loadUserCats(userId: Int, token: String, page: Int = 1, isRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                if (page == 1 && !isRefresh) {
                    val cached = localDataSource.observeCats(userId).firstOrNull() ?: emptyList()
                    if (cached.isNotEmpty()) {
                        _catsData.value = PaginatedData(
                            items = cached.map { it.toModel() },
                            total = cached.size,
                            page = 1,
                            hasMore = true,
                            isLoading = false
                        )
                        _uiState.value = UserUiState.Success
                    }
                }

                _catsData.value = _catsData.value.copy(isLoading = true)

                val response = userRepository.getUserCats(userId, token, page, 20)

                if (response.isSuccessful && response.body()?.success == true) {
                    val cats = response.body()?.data?.cats ?: emptyList()
                    val total = response.body()?.data?.total ?: 0
                    val currentPage = response.body()?.data?.page ?: 1
                    val hasMore = response.body()?.data?.hasMore ?: false

                    val entities = cats.map { cat ->
                        UserCatEntity(
                            id = cat.id,
                            userId = userId,
                            name = cat.name,
                            rarity = cat.rarity,
                            imageUrl = cat.imageUrl,
                            description = cat.description,
                            baseValue = cat.baseValue,
                            obtainedAt = cat.obtainedAt,
                            page = currentPage
                        )
                    }
                    localDataSource.saveCats(entities, currentPage, clearPrevious = (page == 1 || isRefresh))

                    val currentList = if (page == 1 || isRefresh) {
                        emptyList()
                    } else {
                        _catsData.value.items
                    }

                    _catsData.value = PaginatedData(
                        items = currentList + cats,
                        total = total,
                        page = currentPage,
                        hasMore = hasMore,
                        isLoading = false
                    )

                    _uiState.value = UserUiState.Success
                } else {
                    val errorMsg = response.body()?.message ?: "Ошибка загрузки котов"
                    if (_catsData.value.items.isEmpty()) {
                        _uiState.value = UserUiState.Error(errorMsg)
                    }
                    _catsData.value = _catsData.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                if (_catsData.value.items.isEmpty()) {
                    _uiState.value = UserUiState.Error("Ошибка сети: ${e.message}")
                }
                _catsData.value = _catsData.value.copy(isLoading = false)
            }
        }
    }

    fun loadUserQuests(userId: Int, token: String, page: Int = 1, isRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                if (page == 1 && !isRefresh) {
                    val cached = localDataSource.observeQuests(userId).firstOrNull() ?: emptyList()
                    if (cached.isNotEmpty()) {
                        _questsData.value = PaginatedData(
                            items = cached.map { it.toModel() },
                            total = cached.size,
                            page = 1,
                            hasMore = true,
                            isLoading = false
                        )
                        _uiState.value = UserUiState.Success
                    }
                }

                _questsLoadState.value = LoadState.Loading
                _questsData.value = _questsData.value.copy(isLoading = true)

                val response = userRepository.getUserQuests(userId, token, page, 20)

                if (response.isSuccessful && response.body()?.success == true) {
                    val newQuests = response.body()?.data?.quests ?: emptyList()
                    val total = response.body()?.data?.total ?: 0
                    val currentPage = response.body()?.data?.page ?: 1
                    val hasMore = response.body()?.data?.hasMore ?: false

                    val entities = newQuests.map { quest ->
                        UserQuestEntity(
                            id = quest.id,
                            userId = userId,
                            title = quest.title,
                            description = quest.description,
                            questType = quest.questType,
                            rewardStars = quest.rewardStars,
                            userStatus = quest.userStatus ?: "",
                            progress = quest.progress,
                            totalSteps = quest.totalSteps,
                            completedAt = quest.completedAt,
                            districtName = quest.districtName ?: "",
                            isRelevant = quest.isRelevant,
                            page = currentPage
                        )
                    }
                    localDataSource.saveQuests(entities, currentPage, clearPrevious = (page == 1 || isRefresh))

                    val currentList = if (page == 1 || isRefresh) {
                        emptyList()
                    } else {
                        _questsData.value.items
                    }

                    _questsData.value = PaginatedData(
                        items = currentList + newQuests,
                        total = total,
                        page = currentPage,
                        hasMore = hasMore,
                        isLoading = false
                    )

                    _questsLoadState.value = LoadState.Success
                    _uiState.value = UserUiState.Success
                } else {
                    val errorMsg = response.body()?.message ?: "Ошибка загрузки квестов"
                    _questsLoadState.value = LoadState.Error(errorMsg)
                    _questsData.value = _questsData.value.copy(isLoading = false)
                    if (_questsData.value.items.isEmpty()) {
                        _uiState.value = UserUiState.Error(errorMsg)
                    }
                }
            } catch (e: Exception) {
                _questsLoadState.value = LoadState.Error("Ошибка сети: ${e.message}")
                _questsData.value = _questsData.value.copy(isLoading = false)
                if (_questsData.value.items.isEmpty()) {
                    _uiState.value = UserUiState.Error("Ошибка сети: ${e.message}")
                }
            }
        }
    }

    fun refreshAllData(userId: Int, token: String) {
        loadUserProfile(userId, token, silent = true)
        when (selectedMenu.value) {
            MenuMode.ACHIVMENTS -> loadUserAchievements(userId, token, page = 1, isRefresh = true)
            MenuMode.CATS -> loadUserCats(userId, token, page = 1, isRefresh = true)
            MenuMode.QUESTS -> loadUserQuests(userId, token, page = 1, isRefresh = true)
        }
    }

    fun setSelectedMenu(mode: MenuMode) {
        _selectedMenu.value = mode
    }

    fun loadMore(userId: Int, token: String) {
        when (selectedMenu.value) {
            MenuMode.ACHIVMENTS -> {
                if (!_achievementsData.value.isLoading && _achievementsData.value.hasMore) {
                    loadUserAchievements(userId, token, page = _achievementsData.value.page + 1)
                }
            }
            MenuMode.CATS -> {
                if (!_catsData.value.isLoading && _catsData.value.hasMore) {
                    loadUserCats(userId, token, page = _catsData.value.page + 1)
                }
            }
            MenuMode.QUESTS -> {
                if (!_questsData.value.isLoading && _questsData.value.hasMore) {
                    loadUserQuests(userId, token, page = _questsData.value.page + 1)
                }
            }
        }
    }

    suspend fun clearUserData(userId: Int) {
        localDataSource.clearAllUserData(userId)
    }
}

private fun UserAchievementEntity.toModel(): tech.alt255.research.data.model.user.UserAchievement {
    return tech.alt255.research.data.model.user.UserAchievement(
        id = id,
        name = name,
        description = description,
        points = points,
        isCompleted = isCompleted,
        unlockedAt = unlockedAt
    )
}

private fun UserCatEntity.toModel(): tech.alt255.research.data.model.user.UserCat {
    return tech.alt255.research.data.model.user.UserCat(
        id = id,
        name = name,
        rarity = rarity,
        imageUrl = imageUrl,
        description = description,
        baseValue = baseValue,
        obtainedAt = obtainedAt
    )
}

private fun UserQuestEntity.toModel(): UserQuest {
    return UserQuest(
        id = id,
        title = title,
        description = description,
        questType = questType,
        rewardStars = rewardStars,
        userStatus = userStatus,
        progress = progress,
        totalSteps = totalSteps,
        completedAt = completedAt,
        districtName = districtName,
        isRelevant = isRelevant
    )
}

sealed class UserUiState {
    object InitialLoading : UserUiState()
    object Success : UserUiState()
    data class Error(val message: String) : UserUiState()
}

enum class MenuMode {
    QUESTS,
    ACHIVMENTS,
    CATS
}