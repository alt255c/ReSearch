package tech.alt255.research.data.model.notification

import com.google.gson.annotations.SerializedName

data class NotificationResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("timestamp") val timestamp: Long
)