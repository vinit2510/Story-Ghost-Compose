package `in`.vinit.storyghost.data.story

import `in`.vinit.storyghost.data.model.FollowingResponse
import `in`.vinit.storyghost.data.model.HighlightsResponse
import `in`.vinit.storyghost.data.model.UserStoryResponse
import `in`.vinit.storyghost.data.remote.story.StoryAPI

/**
 * Concrete implementation of [StoryRepository] interacting with [StoryAPI].
 */
class StoryRepositoryImpl(
    private val api: StoryAPI
) : StoryRepository {

    override suspend fun getFollowing(userId: String, maxId: String?): FollowingResponse {
        return api.getFollowing(userId = userId, maxId = maxId)
    }

    override suspend fun getStoryByUserId(userId: String): UserStoryResponse {
        return api.getReelsMedia(reelIds = userId)
    }

    override suspend fun getHighlightReelsByUserId(userId: String): HighlightsResponse {
        return api.getHighlightReelsByUserId(userId = userId)
    }

    override suspend fun getHighlightStoryByReelId(highlightReelId: String): UserStoryResponse {
        val reelId = if (highlightReelId.startsWith("highlight:")) highlightReelId else "highlight:$highlightReelId"
        return api.getReelsMedia(reelIds = reelId)
    }
}
