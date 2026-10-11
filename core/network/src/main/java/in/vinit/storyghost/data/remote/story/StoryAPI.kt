package `in`.vinit.storyghost.data.remote.story

import `in`.vinit.storyghost.data.model.HighlightsResponse
import `in`.vinit.storyghost.data.model.StoryListResponse
import `in`.vinit.storyghost.data.model.UserStoryResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface defining endpoints for Instagram story feeds, user stories, and highlights.
 */
interface StoryAPI {

    /**
     * Fetches the home feed reels tray containing current stories from followed accounts.
     *
     * @param queryHash The GraphQL query hash for the story feed query.
     * @param variables Serialized JSON string containing query parameters.
     * @return [StoryListResponse] containing active feed story items.
     */
    @GET("graphql/query/")
    suspend fun getStoryFromFeed(
        @Query("query_hash") queryHash: String,
        @Query("variables") variables: String
    ): StoryListResponse

    /**
     * Fetches active 24-hour stories for a specific user ID.
     *
     * @param queryHash The GraphQL query hash for user story query.
     * @param variables Serialized JSON string containing user ID and overlay options.
     * @return [UserStoryResponse] containing story items for the requested user.
     */
    @GET("graphql/query/")
    suspend fun getStoryByUserId(
        @Query("query_hash") queryHash: String,
        @Query("variables") variables: String
    ): UserStoryResponse

    /**
     * Fetches the list of highlight reels for a given user ID.
     *
     * @param queryHash The GraphQL query hash for highlight list query.
     * @param variables Serialized JSON string specifying user ID and highlight flags.
     * @return [HighlightsResponse] containing highlight reel metadata.
     */
    @GET("graphql/query/")
    suspend fun getHighlightReelsByUserId(
        @Query("query_hash") queryHash: String,
        @Query("variables") variables: String
    ): HighlightsResponse

    /**
     * Fetches story media items contained inside a specific highlight reel ID.
     *
     * @param queryHash The GraphQL query hash for highlight story query.
     * @param variables Serialized JSON string specifying highlight reel ID list.
     * @return [UserStoryResponse] containing media items inside the highlight.
     */
    @GET("graphql/query/")
    suspend fun getHighlightStoryByReelId(
        @Query("query_hash") queryHash: String,
        @Query("variables") variables: String
    ): UserStoryResponse
}