package `in`.vinit.storyghost.data.remote.story

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
        return api.getStoryList()
    }

    override suspend fun getUserStory(userId: String): UserStoryResponse {
        val url = BuildConfig.API_USER_STORY.replace("{{userId}}", userId)
        return api.getUserStory(url)
    }

    override suspend fun getHighlights(userId: String): HighlightsResponse {
        val url = BuildConfig.API_HIGHLIGHT_LIST.replace("{{userId}}", userId)
        return api.getHighlights(url)
    }

    override suspend fun getUserHighlights(highlightReelId: String): UserStoryResponse {
        val url = BuildConfig.API_HIGHLIGHT_STORY.replace("{{highlightReelId}}", highlightReelId)
        return api.getUserHighlights(url)
    }
}