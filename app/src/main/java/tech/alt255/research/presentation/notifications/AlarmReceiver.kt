package tech.alt255.research.presentation.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tech.alt255.research.data.remote.NotificationService

class AlarmReceiver : BroadcastReceiver() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Alarm triggered! Starting network request.")

        coroutineScope.launch {
            try {
                val message = fetchNotificationMessage(context)
                if (message != null) {
                    NotificationHelper.showNotification(context, message)
                } else {
                    Log.e("AlarmReceiver", "Failed to fetch notification message.")
                }
            } catch (e: Exception) {
                Log.e("AlarmReceiver", "Error in alarm processing", e)
            } finally {
                AlarmScheduler.scheduleDailyAlarm(context)
            }
        }
    }

    private suspend fun fetchNotificationMessage(context: Context): String? {
        return withContext(Dispatchers.IO) {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://alt255.tech/ReSearch/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val service = retrofit.create(NotificationService::class.java)
                val response = service.getNotificationMessage()

                if (response.isSuccessful) {
                    response.body()?.message
                } else {
                    Log.e("AlarmReceiver", "Server error: ${response.code()} - ${response.message()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("AlarmReceiver", "Network error", e)
                null
            }
        }
    }
}