package `in`.vinit.storyghost.data.story

import `in`.vinit.storyghost.data.model.HighlightsResponse
import `in`.vinit.storyghost.data.model.StoryListResponse
import `in`.vinit.storyghost.data.model.UserStoryResponse

/**
 * Repository interface providing story feed, user stories, and highlight reel data operations.
 */
interface StoryRepository {

    /**
     * Fetches story feed reels tray for the logged-in user.
     *
     * @return [StoryListResponse] containing active feed story items.
     */
    suspend fun getStoryFromFeed(): StoryListResponse

    /**
     * Fetches active 24-hour stories for a specific user ID.
     *
     * @param userId The target user ID.
     * @return [UserStoryResponse] containing story items.
     */
    suspend fun getStoryByUserId(userId: String): UserStoryResponse

    /**
     * Fetches the list of highlight reels for a given user ID.
     *
     * @param userId The target user ID.
     * @return [HighlightsResponse] containing highlight reel metadata.
     */
    suspend fun getHighlightReelsByUserId(userId: String): HighlightsResponse

    /**
     * Fetches story media items contained inside a specific highlight reel ID.
     *
     * @param highlightReelId The highlight reel ID.
     * @return [UserStoryResponse] containing media items inside the highlight.
     */
    suspend fun getHighlightStoryByReelId(highlightReelId: String): UserStoryResponse
}
