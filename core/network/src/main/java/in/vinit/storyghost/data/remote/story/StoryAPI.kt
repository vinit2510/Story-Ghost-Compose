package `in`.vinit.storyghost.data.remote.story

import `in`.vinit.storyghost.data.model.FollowingResponse
import `in`.vinit.storyghost.data.model.HighlightsResponse
import `in`.vinit.storyghost.data.model.UserStoryResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface defining endpoints for Instagram story feeds, user stories, and highlights.
 */
interface StoryAPI {

    /**
     * Fetches the following list for a user containing active 24-hour story timestamps.
     *
     * @param userId The Instagram user ID whose following list is retrieved.
     * @param maxId Optional pagination cursor.
     * @return [FollowingResponse] containing followed users.
     */
    @GET("api/v1/friendships/{userId}/following/")
    suspend fun getFollowing(
        @Path("userId") userId: String,
        @Query("max_id") maxId: String? = null
    ): FollowingResponse

    /**
     * Fetches story media items using Instagram native reels_media endpoint.
     *
     * @param reelIds Comma-separated or single reel ID (e.g. user ID or "highlight:12345").
     * @return [UserStoryResponse] containing story reel media.
     */
    @GET("api/v1/feed/reels_media/")
    suspend fun getReelsMedia(
        @Query("reel_ids") reelIds: String
    ): UserStoryResponse

    /**
     * Fetches highlight reels for a given user ID via Instagram highlights tray endpoint.
     *
     * @param userId The Instagram user ID whose highlight tray is retrieved.
     * @return [HighlightsResponse] containing highlight reel metadata.
     */
    @GET("api/v1/highlights/{userId}/highlights_tray/")
    suspend fun getHighlightReelsByUserId(
        @Path("userId") userId: String
    ): HighlightsResponse
}
