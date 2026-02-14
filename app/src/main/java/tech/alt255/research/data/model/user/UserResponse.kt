package tech.alt255.research.data.model.user

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("data") val data: UserData? = null
)

data class UserData(
    @SerializedName("profile") val profile: UserProfile? = null,
    @SerializedName("achievements") val achievements: List<UserAchievement>? = null,
    @SerializedName("cats") val cats: List<UserCat>? = null,
    @SerializedName("quests") val quests: List<UserQuest>? = null,
    @SerializedName("total") val total: Int? = null,
    @SerializedName("page") val page: Int? = null,
    @SerializedName("has_more") val hasMore: Boolean? = null
)

data class UserProfile(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("user_name") val userName: String,
    @SerializedName("user_nickname") val userNickname: String,
    @SerializedName("user_photo") val userPhoto: String,
    @SerializedName("stars") val stars: Int,
    @SerializedName("level") val level: Int,
    @SerializedName("next_level_stars") val nextLevelStars: Int
)

data class UserAchievement(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("points") val points: Int,
    @SerializedName("is_completed") val isCompleted: Boolean,
    @SerializedName("unlocked_at") val unlockedAt: String?
)

data class UserCat(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("rarity") val rarity: String,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("description") val description: String,
    @SerializedName("base_value") val baseValue: Int,
    @SerializedName("obtained_at") val obtainedAt: String
)

data class UserQuest(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("quest_type") val questType: String,
    @SerializedName("reward_stars") val rewardStars: Int,
    @SerializedName("status") val status: String,
    @SerializedName("progress") val progress: Int,
    @SerializedName("max_progress") val maxProgress: Int,
    @SerializedName("completed_at") val completedAt: String?
)

data class UserDataRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("token") val token: String,
    @SerializedName("action") val action: String,
    @SerializedName("page") val page: Int? = null,
    @SerializedName("limit") val limit: Int? = null
)

data class UpdateProfileRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("token") val token: String,
    @SerializedName("action") val action: String,
    @SerializedName("user_name") val userName: String? = null,
    @SerializedName("user_nickname") val userNickname: String? = null
)

data class UpdatePasswordRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("token") val token: String,
    @SerializedName("action") val action: String,
    @SerializedName("current_password") val currentPassword: String,
    @SerializedName("new_password") val newPassword: String
)

data class UpdateAvatarRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("token") val token: String,
    @SerializedName("action") val action: String,
    @SerializedName("avatar_base64") val avatarBase64: String
)

data class PaginatedData<T>(
    val items: List<T>,
    val total: Int,
    val page: Int,
    val hasMore: Boolean,
    val isLoading: Boolean = false
)

sealed class LoadState {
    object Initial : LoadState()
    object Loading : LoadState()
    object Success : LoadState()
    data class Error(val message: String) : LoadState()
}