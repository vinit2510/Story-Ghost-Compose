package `in`.vinit.storyghost.data.remote.story

import com.google.gson.Gson
import `in`.vinit.storyghost.BuildConfig
import `in`.vinit.storyghost.data.model.HighlightsResponse
import `in`.vinit.storyghost.data.model.StoryListResponse
import `in`.vinit.storyghost.data.model.UserStoryResponse

interface StoryRepository {
    suspend fun getStoryList(): StoryListResponse
    suspend fun getUserStory(userId: String): UserStoryResponse
    suspend fun getHighlights(userId: String): HighlightsResponse
    suspend fun getUserHighlights(highlightReelId: String): UserStoryResponse
}

class StoryRepositoryImpl(private val api: StoryAPI) : StoryRepository {

    override suspend fun getStoryList(): StoryListResponse {
        val queryHash = BuildConfig.QH_STORY_LIST
        val vars = mapOf(
            "has_stories" to true,
            "fetch_media_item_count" to 0
        )
        return api.getStoryList(queryHash, Gson().toJson(vars))
    }

    override suspend fun getUserStory(userId: String): UserStoryResponse {
        val queryHash = BuildConfig.QH_USER_STORY
        val vars = mapOf(
            "reel_ids" to listOf(userId),
            "precomposed_overlay" to false
        )
        return api.getUserStory(queryHash, Gson().toJson(vars))
    }

    override suspend fun getHighlights(userId: String): HighlightsResponse {
        val queryHash = BuildConfig.QH_HIGHLIGHT_LIST
        val vars = mapOf(
            "user_id" to userId,
            "include_chaining" to false,
            "include_reel" to false,
            "include_suggested_users" to false,
            "include_logged_out_extras" to false,
            "include_highlight_reels" to true,
            "include_live_status" to false
        )
        return api.getHighlights(queryHash, Gson().toJson(vars))
    }

    override suspend fun getUserHighlights(highlightReelId: String): UserStoryResponse {
        val queryHash = BuildConfig.QH_HIGHLIGHT_STORY
        val vars = mapOf(
            "highlight_reel_ids" to listOf(highlightReelId),
            "precomposed_overlay" to false
        )
        return api.getUserHighlights(queryHash, Gson().toJson(vars))
    }
}