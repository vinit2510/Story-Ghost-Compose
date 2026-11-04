package `in`.vinit.storyghost.data.remote.story

import `in`.vinit.storyghost.data.model.FollowingResponse
import `in`.vinit.storyghost.data.model.HighlightsResponse
import `in`.vinit.storyghost.data.model.UserStoryResponse
import `in`.vinit.storyghost.data.story.StoryRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class StoryRepositoryTest {

    private val api: StoryAPI = mockk()
    private lateinit var repository: StoryRepositoryImpl

    @Before
    fun setUp() {
        repository = StoryRepositoryImpl(api)
    }

    @Test
    fun `getFollowing delegates to API with expected params`() = runTest {
        val expected = FollowingResponse()
        coEvery { api.getFollowing("123", "cursor_1") } returns expected

        val result = repository.getFollowing("123", "cursor_1")

        assertNotNull(result)
        assertEquals(expected, result)
        coVerify(exactly = 1) { api.getFollowing("123", "cursor_1") }
    }

    @Test
    fun `getStoryByUserId delegates to native reels_media endpoint`() = runTest {
        val userId = "user_99"
        val expected = UserStoryResponse()
        coEvery { api.getReelsMedia(userId) } returns expected

        val result = repository.getStoryByUserId(userId)

        assertNotNull(result)
        assertEquals(expected, result)
        coVerify(exactly = 1) { api.getReelsMedia(userId) }
    }

    @Test
    fun `getHighlightReelsByUserId delegates to highlights_tray endpoint`() = runTest {
        val userId = "user_99"
        val expected = HighlightsResponse()
        coEvery { api.getHighlightReelsByUserId(userId) } returns expected

        val result = repository.getHighlightReelsByUserId(userId)

        assertNotNull(result)
        assertEquals(expected, result)
        coVerify(exactly = 1) { api.getHighlightReelsByUserId(userId) }
    }

    @Test
    fun `getHighlightStoryByReelId appends highlight prefix and delegates to reels_media`() = runTest {
        val rawReelId = "12345"
        val expected = UserStoryResponse()
        coEvery { api.getReelsMedia("highlight:12345") } returns expected

        val result = repository.getHighlightStoryByReelId(rawReelId)

        assertNotNull(result)
        assertEquals(expected, result)
        coVerify(exactly = 1) { api.getReelsMedia("highlight:12345") }
    }

    @Test
    fun `getHighlightStoryByReelId preserves existing highlight prefix`() = runTest {
        val prefixedReelId = "highlight:67890"
        val expected = UserStoryResponse()
        coEvery { api.getReelsMedia("highlight:67890") } returns expected

        val result = repository.getHighlightStoryByReelId(prefixedReelId)

        assertNotNull(result)
        assertEquals(expected, result)
        coVerify(exactly = 1) { api.getReelsMedia("highlight:67890") }
    }
}
