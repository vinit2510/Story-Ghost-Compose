package `in`.vinit.storyghost.data.remote.story

import `in`.vinit.storyghost.config.AppConfig
import `in`.vinit.storyghost.data.model.FollowingResponse
import `in`.vinit.storyghost.data.model.HighlightsResponse
import `in`.vinit.storyghost.data.model.StoryListResponse
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
    private val appConfig: AppConfig = mockk(relaxed = true)
    private lateinit var repository: StoryRepositoryImpl

    @Before
    fun setUp() {
        repository = StoryRepositoryImpl(api, appConfig)
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

    @Suppress("DEPRECATION")
    @Test
    fun `getStoryFromFeed delegates to API with expected params`() = runTest {
        val expected = StoryListResponse()
        coEvery { api.getStoryFromFeed(any(), any()) } returns expected

        val result = repository.getStoryFromFeed()

        assertNotNull(result)
        assertEquals(expected, result)
        coVerify(exactly = 1) { api.getStoryFromFeed(any(), any()) }
    }

    @Test
    fun `getStoryByUserId delegates to API with user ID`() = runTest {
        val userId = "user_99"
        val expected = UserStoryResponse()
        coEvery { api.getStoryByUserId(any(), any()) } returns expected

        val result = repository.getStoryByUserId(userId)

        assertNotNull(result)
        assertEquals(expected, result)
        coVerify(exactly = 1) { api.getStoryByUserId(any(), any()) }
    }

    @Test
    fun `getHighlightReelsByUserId delegates to API with user ID`() = runTest {
        val userId = "user_99"
        val expected = HighlightsResponse()
        coEvery { api.getHighlightReelsByUserId(any(), any()) } returns expected

        val result = repository.getHighlightReelsByUserId(userId)

        assertNotNull(result)
        assertEquals(expected, result)
        coVerify(exactly = 1) { api.getHighlightReelsByUserId(any(), any()) }
    }

    @Test
    fun `getHighlightStoryByReelId delegates to API with highlight reel ID`() = runTest {
        val reelId = "highlight_123"
        val expected = UserStoryResponse()
        coEvery { api.getHighlightStoryByReelId(any(), any()) } returns expected

        val result = repository.getHighlightStoryByReelId(reelId)

        assertNotNull(result)
        assertEquals(expected, result)
        coVerify(exactly = 1) { api.getHighlightStoryByReelId(any(), any()) }
    }
}
