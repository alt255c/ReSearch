package tech.alt255.research.data.model.quest

import com.google.gson.annotations.SerializedName

data class QuestResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("data") val data: QuestData? = null
)

data class QuestData(
    @SerializedName("quests") val quests: List<AvailableQuest>? = null,
    @SerializedName("total") val total: Int? = null,
    @SerializedName("page") val page: Int? = null,
    @SerializedName("has_more") val hasMore: Boolean? = null,
    @SerializedName("limit") val limit: Int? = null,

    @SerializedName("preview") val preview: QuestPreview? = null,

    @SerializedName("step") val step: QuestStep? = null,

    @SerializedName("result") val result: SubmitResult? = null,
    @SerializedName("quest_id") val questId: Int? = null,

    @SerializedName("id") val id: Int? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("quest_type") val questType: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("end_date") val endDate: String? = null,
    @SerializedName("is_accepted") val isAccepted: Boolean? = null,
    @SerializedName("user_status") val userStatus: String? = null,
    @SerializedName("reward_stars") val rewardStars: Int? = null,
    @SerializedName("reward_cat") val rewardCat: CatReward? = null,
    @SerializedName("district_name") val districtName: String? = null,
    @SerializedName("steps_count") val stepsCount: Int? = null,

    @SerializedName("step_id") val stepId: Int? = null,
    @SerializedName("step_number") val stepNumber: Int? = null,
    @SerializedName("task_description") val taskDescription: String? = null,
    @SerializedName("task_type") val taskType: String? = null,
    @SerializedName("points") val points: Int? = null,
    @SerializedName("current_progress") val currentProgress: Int? = null,

    @SerializedName("is_final_step") val isFinalStep: Boolean? = null,
    @SerializedName("reward") val reward: StepReward? = null
)

data class AvailableQuest(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("short_description") val shortDescription: String,
    @SerializedName("quest_type") val questType: String,
    @SerializedName("reward_stars") val rewardStars: Int,
    @SerializedName("district_name") val districtName: String?,
    @SerializedName("status") val status: String,
    @SerializedName("is_accepted") val isAccepted: Boolean,
    @SerializedName("user_status") val userStatus: String?
)

data class QuestPreview(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("quest_type") val questType: String,
    @SerializedName("status") val status: String,
    @SerializedName("end_date") val endDate: String?,
    @SerializedName("is_accepted") val isAccepted: Boolean,
    @SerializedName("user_status") val userStatus: String?,
    @SerializedName("reward_stars") val rewardStars: Int,
    @SerializedName("reward_cat") val rewardCat: CatReward?,
    @SerializedName("district_name") val districtName: String?,
    @SerializedName("steps_count") val stepsCount: Int
)

data class CatReward(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("rarity") val rarity: String? = null,
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("compensation") val compensation: Int? = null,
    @SerializedName("message") val message: String? = null
)

data class QuestStep(
    @SerializedName("step_id") val stepId: Int,
    @SerializedName("step_number") val stepNumber: Int,
    @SerializedName("task_description") val taskDescription: String,
    @SerializedName("task_type") val taskType: String,
    @SerializedName("points") val points: Int,
    @SerializedName("current_progress") val currentProgress: Int,
    @SerializedName("user_status") val userStatus: String
)

data class SubmitResult(
    @SerializedName("is_final_step") val isFinalStep: Boolean,
    @SerializedName("reward") val reward: StepReward?
)

data class StepReward(
    @SerializedName("stars_earned") val starsEarned: Int,
    @SerializedName("cat_reward") val catReward: CatReward?,
    @SerializedName("quest_type") val questType: String
)

data class QuestRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("token") val token: String,
    @SerializedName("action") val action: String,
    @SerializedName("page") val page: Int? = null,
    @SerializedName("limit") val limit: Int? = null,
    @SerializedName("quest_id") val questId: Int? = null,
    @SerializedName("step_number") val stepNumber: Int? = null,
    @SerializedName("answer") val answer: String? = null
)

data class UserQuestResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("data") val data: UserQuestData? = null
)

data class UserQuestData(
    @SerializedName("quests") val quests: List<UserQuest>,
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("has_more") val hasMore: Boolean,
    @SerializedName("limit") val limit: Int
)

data class UserQuest(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("quest_type") val questType: String,
    @SerializedName("reward_stars") val rewardStars: Int,
    @SerializedName("district_name") val districtName: String?,
    @SerializedName("user_status") val userStatus: String?,
    @SerializedName("progress") val progress: Int,
    @SerializedName("total_steps") val totalSteps: Int,
    @SerializedName("completed_at") val completedAt: String?,
    @SerializedName("is_relevant") val isRelevant: Boolean
)