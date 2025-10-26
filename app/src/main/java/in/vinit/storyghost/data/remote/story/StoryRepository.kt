package `in`.vinit.storyghost.data.remote.story

import `in`.vinit.storyghost.data.model.HighlightsResponse
import `in`.vinit.storyghost.data.model.StoryListResponse
import `in`.vinit.storyghost.data.model.UserStoryResponse
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.util.makeApiCall

interface StoryRepository {
    suspend fun getStoryList(): UiState<StoryListResponse>
    suspend fun getUserStory(url: String): UiState<UserStoryResponse>
    suspend fun getHighlights(url: String): UiState<HighlightsResponse>
    suspend fun getUserHighlights(url: String): UiState<UserStoryResponse>
}

class StoryRepositoryImpl(private val api: StoryAPI) : StoryRepository {
    override suspend fun getStoryList() = makeApiCall { api.getStoryList() }
    override suspend fun getUserStory(url: String) = makeApiCall { api.getUserStory(url) }
    override suspend fun getHighlights(url: String) = makeApiCall { api.getHighlights(url) }
    override suspend fun getUserHighlights(url: String) = makeApiCall { api.getUserHighlights(url) }
}