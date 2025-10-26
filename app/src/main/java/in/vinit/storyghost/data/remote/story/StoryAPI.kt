package `in`.vinit.storyghost.data.remote.story

import `in`.vinit.storyghost.BuildConfig
import `in`.vinit.storyghost.data.model.HighlightsResponse
import `in`.vinit.storyghost.data.model.StoryListResponse
import `in`.vinit.storyghost.data.model.UserStoryResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface StoryAPI {

    @GET(BuildConfig.API_STORY_LIST)
    suspend fun getStoryList(): StoryListResponse

    @GET
    suspend fun getUserStory(@Url url: String): UserStoryResponse

    @GET
    suspend fun getHighlights(@Url url: String): HighlightsResponse

    @GET
    suspend fun getUserHighlights(@Url url: String): UserStoryResponse
}