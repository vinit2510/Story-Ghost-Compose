package `in`.vinit.storyghost.data.story

import com.google.gson.Gson
import `in`.vinit.storyghost.config.AppConfig
import `in`.vinit.storyghost.data.model.HighlightsResponse
import `in`.vinit.storyghost.data.model.StoryListResponse
import `in`.vinit.storyghost.data.model.UserStoryResponse
import `in`.vinit.storyghost.data.remote.story.StoryAPI

/**
 * Concrete implementation of [StoryRepository] interacting with [in.vinit.storyghost.data.remote.story.StoryAPI].
 */
class StoryRepositoryImpl(
    private val api: StoryAPI,
    private val appConfig: AppConfig
) : StoryRepository {

    override suspend fun getStoryFromFeed(): StoryListResponse {
        val queryHash = appConfig.qhStoryList
        val vars = mapOf(
            "has_stories" to true,
            "fetch_media_item_count" to 0
        )
        return api.getStoryFromFeed(queryHash, Gson().toJson(vars))
    }

    override suspend fun getStoryByUserId(userId: String): UserStoryResponse {
        val queryHash = appConfig.qhUserStory
        val vars = mapOf(
            "reel_ids" to listOf(userId),
            "precomposed_overlay" to false
        )
        return api.getStoryByUserId(queryHash, Gson().toJson(vars))
    }

    override suspend fun getHighlightReelsByUserId(userId: String): HighlightsResponse {
        val queryHash = appConfig.qhHighlightList
        val vars = mapOf(
            "user_id" to userId,
            "include_chaining" to false,
            "include_reel" to false,
            "include_suggested_users" to false,
            "include_logged_out_extras" to false,
            "include_highlight_reels" to true,
            "include_live_status" to false
        )
        return api.getHighlightReelsByUserId(queryHash, Gson().toJson(vars))
    }

    override suspend fun getHighlightStoryByReelId(highlightReelId: String): UserStoryResponse {
        val queryHash = appConfig.qhHighlightStory
        val vars = mapOf(
            "highlight_reel_ids" to listOf(highlightReelId),
            "precomposed_overlay" to false
        )
        return api.getHighlightStoryByReelId(queryHash, Gson().toJson(vars))
    }
}
