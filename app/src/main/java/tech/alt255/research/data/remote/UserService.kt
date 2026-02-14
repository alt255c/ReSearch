package tech.alt255.research.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import tech.alt255.research.data.model.user.UpdateAvatarRequest
import tech.alt255.research.data.model.user.UpdatePasswordRequest
import tech.alt255.research.data.model.user.UpdateProfileRequest
import tech.alt255.research.data.model.user.UserDataRequest
import tech.alt255.research.data.model.user.UserResponse

interface UserService {
    @POST("user_service.php")
    suspend fun getUserProfile(@Body request: UserDataRequest): Response<UserResponse>

    @POST("user_service.php")
    suspend fun getUserAchievements(@Body request: UserDataRequest): Response<UserResponse>

    @POST("user_service.php")
    suspend fun getUserCats(@Body request: UserDataRequest): Response<UserResponse>

    @POST("user_service.php")
    suspend fun getUserQuests(@Body request: UserDataRequest): Response<UserResponse>

    @POST("user_service.php")
    suspend fun updateUserProfile(@Body request: UpdateProfileRequest): Response<UserResponse>

    @POST("user_service.php")
    suspend fun updatePassword(@Body request: UpdatePasswordRequest): Response<UserResponse>

    @POST("user_service.php")
    suspend fun updateAvatar(@Body request: UpdateAvatarRequest): Response<UserResponse>
}