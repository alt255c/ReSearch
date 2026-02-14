package tech.alt255.research.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import tech.alt255.research.data.model.quest.QuestRequest
import tech.alt255.research.data.model.quest.QuestResponse
import tech.alt255.research.data.model.quest.UserQuestResponse

interface QuestService {
    @POST("quest_service.php")
    suspend fun getAvailableQuests(@Body request: QuestRequest): Response<QuestResponse>

    @POST("quest_service.php")
    suspend fun getUserQuests(@Body request: QuestRequest): Response<UserQuestResponse>

    @POST("quest_service.php")
    suspend fun getQuestPreview(@Body request: QuestRequest): Response<QuestResponse>

    @POST("quest_service.php")
    suspend fun acceptQuest(@Body request: QuestRequest): Response<QuestResponse>

    @POST("quest_service.php")
    suspend fun getQuestStep(@Body request: QuestRequest): Response<QuestResponse>

    @POST("quest_service.php")
    suspend fun submitQuestStep(@Body request: QuestRequest): Response<QuestResponse>
}