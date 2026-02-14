package tech.alt255.research.data.remote

import retrofit2.Response
import retrofit2.http.GET
import tech.alt255.research.data.model.notification.NotificationResponse

interface NotificationService {
    @GET("notification_service.php")
    suspend fun getNotificationMessage(): Response<NotificationResponse>
}