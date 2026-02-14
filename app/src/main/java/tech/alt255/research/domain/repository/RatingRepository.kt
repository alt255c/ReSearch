package tech.alt255.research.domain.repository

import retrofit2.Response
import tech.alt255.research.data.model.rating.RatingResponse
import tech.alt255.research.data.remote.RatingRequest
import tech.alt255.research.data.remote.RatingService
import javax.inject.Inject

class RatingRepository @Inject constructor(
    private val ratingService: RatingService
) {
    suspend fun getRating(userId: Int, token: String, page: Int, limit: Int): Response<RatingResponse> {
        return ratingService.getRating(
            RatingRequest(
                action = "get_rating",
                userId = userId,
                token = token,
                page = page,
                limit = limit
            )
        )
    }
}