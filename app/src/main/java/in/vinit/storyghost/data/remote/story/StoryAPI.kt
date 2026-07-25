package `in`.vinit.storyghost.data.remote.story

import `in`.vinit.storyghost.data.model.HighlightsResponse
import `in`.vinit.storyghost.data.model.StoryListResponse
import `in`.vinit.storyghost.data.model.UserStoryResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface StoryAPI {

    @GET("graphql/query/")
    suspend fun getStoryList(
        @Query("query_hash") queryHash: String,
        @Query("variables") variables: String
    ): StoryListResponse

    @GET("graphql/query/")
    suspend fun getUserStory(
        @Query("query_hash") queryHash: String,
        @Query("variables") variables: String
    ): UserStoryResponse

    @GET("graphql/query/")
    suspend fun getHighlights(
        @Query("query_hash") queryHash: String,
        @Query("variables") variables: String
    ): HighlightsResponse

    @GET("graphql/query/")
    suspend fun getUserHighlights(
        @Query("query_hash") queryHash: String,
        @Query("variables") variables: String
    ): UserStoryResponse
}