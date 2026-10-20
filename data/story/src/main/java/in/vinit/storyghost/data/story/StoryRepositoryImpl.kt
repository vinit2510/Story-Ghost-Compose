package `in`.vinit.storyghost.data.story

import com.google.gson.Gson
import `in`.vinit.storyghost.config.AppConfig
import `in`.vinit.storyghost.data.model.FollowingResponse
import `in`.vinit.storyghost.data.model.HighlightsResponse
import `in`.vinit.storyghost.data.model.StoryListResponse
import `in`.vinit.storyghost.data.model.UserStoryResponse
import `in`.vinit.storyghost.data.remote.story.StoryAPI

/**
 * Concrete implementation of [StoryRepository] interacting with [StoryAPI].
 */
class StoryRepositoryImpl(
    private val api: StoryAPI,
    private val appConfig: AppConfig
) : StoryRepository {

    override suspend fun getFollowing(userId: String, maxId: String?): FollowingResponse {
        val base =
            if (appConfig.iBaseUrl.endsWith("/")) appConfig.iBaseUrl else "${appConfig.iBaseUrl}/"
        val url = "${base}api/v1/friendships/$userId/following/"
        return api.getFollowing(url, maxId)
    }

    @Deprecated("Deprecated by Instagram GraphQL changes. Use getFollowing instead.")
    @Suppress("DEPRECATION")
    override suspend fun getStoryFromFeed(): StoryListResponse {
        val queryHash = appConfig.qhStoryList
        val vars = mapOf(
            "has_stories" to true,
            "fetch_media_item_count" to 0
        )
        return api.getStoryFromFeed(queryHash, Gson().toJson(vars))
    }

    override suspend fun getStoryByUserId(userId: String): UserStoryResponse {
        val vars = mapOf(
            "media_id" to null,
            "reel_ids_arr" to listOf(userId),
            "__relay_internal__pv__PolarisCommunityNoteStoriesLabelEnabledrelayprovider" to false
        )
        return api.getStories(appConfig.docIdStoryAndHighlight, Gson().toJson(vars))
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
        val reelId = if (highlightReelId.startsWith("highlight:")) highlightReelId else "highlight:$highlightReelId"
        val vars = mapOf(
            "media_id" to null,
            "reel_ids_arr" to listOf(reelId),
            "__relay_internal__pv__PolarisCommunityNoteStoriesLabelEnabledrelayprovider" to false
        )
        return api.getStories(appConfig.docIdStoryAndHighlight, Gson().toJson(vars))
    }
}
