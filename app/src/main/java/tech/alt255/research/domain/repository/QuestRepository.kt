package tech.alt255.research.domain.repository

import retrofit2.Response
import tech.alt255.research.data.model.quest.QuestRequest
import tech.alt255.research.data.model.quest.QuestResponse
import tech.alt255.research.data.model.quest.UserQuestResponse
import tech.alt255.research.data.remote.QuestService
import javax.inject.Inject

class QuestRepository @Inject constructor(
    private val questService: QuestService
) {
    suspend fun getAvailableQuests(
        userId: Int,
        token: String,
        page: Int = 1,
        limit: Int = 20
    ): Response<QuestResponse> {
        return questService.getAvailableQuests(
            QuestRequest(userId, token, "get_available_quests", page, limit)
        )
    }

    suspend fun getUserQuests(
        userId: Int,
        token: String,
        page: Int = 1,
        limit: Int = 20
    ): Response<UserQuestResponse> {
        return questService.getUserQuests(
            QuestRequest(userId, token, "get_user_quests", page, limit)
        )
    }

    suspend fun getQuestPreview(
        userId: Int,
        token: String,
        questId: Int
    ): Response<QuestResponse> {
        return questService.getQuestPreview(
            QuestRequest(userId, token, "get_quest_preview", questId = questId)
        )
    }

    suspend fun acceptQuest(
        userId: Int,
        token: String,
        questId: Int
    ): Response<QuestResponse> {
        return questService.acceptQuest(
            QuestRequest(userId, token, "accept_quest", questId = questId)
        )
    }

    suspend fun getQuestStep(
        userId: Int,
        token: String,
        questId: Int,
        stepNumber: Int
    ): Response<QuestResponse> {
        return questService.getQuestStep(
            QuestRequest(userId, token, "get_quest_step", questId = questId, stepNumber = stepNumber)
        )
    }

    suspend fun submitQuestStep(
        userId: Int,
        token: String,
        questId: Int,
        stepNumber: Int,
        answer: String
    ): Response<QuestResponse> {
        return questService.submitQuestStep(
            QuestRequest(userId, token, "submit_quest_step", questId = questId, stepNumber = stepNumber, answer = answer)
        )
    }
}