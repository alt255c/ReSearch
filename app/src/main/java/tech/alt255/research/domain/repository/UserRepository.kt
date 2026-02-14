package tech.alt255.research.domain.repository

import retrofit2.Response
import tech.alt255.research.data.model.quest.UserQuestResponse
import tech.alt255.research.data.model.user.UpdateAvatarRequest
import tech.alt255.research.data.model.user.UpdatePasswordRequest
import tech.alt255.research.data.model.user.UpdateProfileRequest
import tech.alt255.research.data.model.user.UserDataRequest
import tech.alt255.research.data.model.user.UserResponse
import tech.alt255.research.data.remote.UserService
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: UserService,
    private val questRepository: QuestRepository
) {
    suspend fun getUserProfile(userId: Int, token: String): Response<UserResponse> {
        return userService.getUserProfile(UserDataRequest(userId, token, "get_profile"))
    }

    suspend fun getUserAchievements(
        userId: Int,
        token: String,
        page: Int = 1,
        limit: Int = 20
    ): Response<UserResponse> {
        return userService.getUserAchievements(
            UserDataRequest(userId, token, "get_achievements", page, limit)
        )
    }

    suspend fun getUserCats(
        userId: Int,
        token: String,
        page: Int = 1,
        limit: Int = 20
    ): Response<UserResponse> {
        return userService.getUserCats(
            UserDataRequest(userId, token, "get_cats", page, limit)
        )
    }

    suspend fun getUserQuests(
        userId: Int,
        token: String,
        page: Int = 1,
        limit: Int = 20
    ): Response<UserQuestResponse> {
        return questRepository.getUserQuests(userId, token, page, limit)
    }

    suspend fun updateUserProfile(
        userId: Int,
        token: String,
        userName: String,
        userNickname: String
    ): Response<UserResponse> {
        return userService.updateUserProfile(
            UpdateProfileRequest(
                userId = userId,
                token = token,
                action = "update_profile",
                userName = userName,
                userNickname = userNickname
            )
        )
    }

    suspend fun updatePassword(
        userId: Int,
        token: String,
        currentPassword: String,
        newPassword: String
    ): Response<UserResponse> {
        return userService.updatePassword(
            UpdatePasswordRequest(
                userId = userId,
                token = token,
                action = "update_password",
                currentPassword = currentPassword,
                newPassword = newPassword
            )
        )
    }

    suspend fun updateAvatar(
        userId: Int,
        token: String,
        avatarBase64: String
    ): Response<UserResponse> {
        return userService.updateAvatar(
            UpdateAvatarRequest(
                userId = userId,
                token = token,
                action = "update_avatar",
                avatarBase64 = avatarBase64
            )
        )
    }
}