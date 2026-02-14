package tech.alt255.research.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurePrefs @Inject constructor(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD = "password"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_TOKEN = "token"
    }

    fun saveCredentials(email: String, password: String, userId: Int = 0) {
        sharedPreferences.edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_PASSWORD, password)
            .putInt(KEY_USER_ID, userId)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }

    fun getCredentials(): Triple<String, String, Int>? {
        val email = sharedPreferences.getString(KEY_EMAIL, null)
        val password = sharedPreferences.getString(KEY_PASSWORD, null)
        val userId = sharedPreferences.getInt(KEY_USER_ID, 0)
        return if (email != null && password != null) {
            Triple(email, password, userId)
        } else {
            null
        }
    }

    fun getEmail(): String? = sharedPreferences.getString(KEY_EMAIL, null)
    fun getUserId(): Int = sharedPreferences.getInt(KEY_USER_ID, 0)
    fun isLoggedIn(): Boolean = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)

    fun clearCredentials() {
        sharedPreferences.edit()
            .remove(KEY_EMAIL)
            .remove(KEY_PASSWORD)
            .remove(KEY_USER_ID)
            .remove(KEY_TOKEN)
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .apply()
    }

    fun saveUserId(userId: Int) {
        sharedPreferences.edit()
            .putInt(KEY_USER_ID, userId)
            .apply()
    }

    fun saveToken(token: String) {
        sharedPreferences.edit()
            .putString(KEY_TOKEN, token)
            .apply()
    }

    fun getToken(): String? = sharedPreferences.getString(KEY_TOKEN, null)

    fun clearToken() {
        sharedPreferences.edit()
            .remove(KEY_TOKEN)
            .apply()
    }

    fun logout() {
        clearCredentials()
    }
}