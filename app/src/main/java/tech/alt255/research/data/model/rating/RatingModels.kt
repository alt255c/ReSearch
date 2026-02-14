package tech.alt255.research.data.model.rating

import com.google.gson.annotations.SerializedName

data class RatingResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("data") val data: RatingData
)

data class RatingData(
    @SerializedName("users") val users: List<RatingUser>,
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("has_more") val hasMore: Boolean,
    @SerializedName("current_user_rank") val currentUserRank: Int?
)

data class RatingUser(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("photo") val photo: String,
    @SerializedName("stars") val stars: Int,
    @SerializedName("rank") val rank: Int
)