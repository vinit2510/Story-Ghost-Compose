package `in`.vinit.storyghost.data.remote.story

import `in`.vinit.storyghost.data.model.FollowingResponse
import `in`.vinit.storyghost.data.model.HighlightsResponse
import `in`.vinit.storyghost.data.model.UserStoryResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Retrofit interface defining endpoints for Instagram story feeds, user stories, and highlights.
 */
interface StoryAPI {

    /**
     * Fetches the following list for a user containing active 24-hour story timestamps.
     *
     * @param url Dynamic target URL for the following endpoint.
     * @param maxId Optional pagination cursor.
     * @return [FollowingResponse] containing followed users.
     */
    @GET
    suspend fun getFollowing(
        @Url url: String,
        @Query("max_id") maxId: String? = null
    ): FollowingResponse

    /**
     * Fetches story or highlight media items using Instagram Relay doc_id query.
     *
     * @param docId The persistent GraphQL document ID (e.g., PolarisStoriesV3ReelPageStandaloneQuery).
     * @param variables Serialized JSON string containing query parameters.
     * @return [UserStoryResponse] containing story items and tappable mentions.
     */
    @FormUrlEncoded
    @POST("graphql/query")
    suspend fun getStories(
        @Field("doc_id") docId: String,
        @Field("variables") variables: String
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
}
