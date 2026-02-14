package tech.alt255.research.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import tech.alt255.research.data.model.rating.RatingResponse
import com.google.gson.annotations.SerializedName

data class RatingRequest(
    @SerializedName("action") val action: String,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("token") val token: String,
    @SerializedName("page") val page: Int? = null,
    @SerializedName("limit") val limit: Int? = null
)

interface RatingService {
    @POST("rating_service.php")
    suspend fun getRating(@Body request: RatingRequest): Response<RatingResponse>
}