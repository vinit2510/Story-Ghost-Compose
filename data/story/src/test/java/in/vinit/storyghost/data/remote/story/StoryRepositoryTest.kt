package `in`.vinit.storyghost.data.remote.story

import `in`.vinit.storyghost.config.AppConfig
import `in`.vinit.storyghost.data.model.FollowingResponse
import `in`.vinit.storyghost.data.model.HighlightsResponse
import `in`.vinit.storyghost.data.model.UserStoryResponse
import `in`.vinit.storyghost.data.story.StoryRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
        every { appConfig.iBaseUrl } returns "https://i.example.com/"
        repository = StoryRepositoryImpl(api, appConfig)
    }

    @Test
    fun `getFollowing delegates to API with expected params`() = runTest {
        val expected = FollowingResponse()
        val expectedUrl = "https://i.example.com/api/v1/friendships/123/following/"
        coEvery { api.getFollowing(expectedUrl, "cursor_1") } returns expected

        val result = repository.getFollowing("123", "cursor_1")

        assertNotNull(result)
        assertEquals(expected, result)
        coVerify(exactly = 1) { api.getFollowing(expectedUrl, "cursor_1") }
    }

    @Test
    fun `getFollowing handles iBaseUrl without trailing slash`() = runTest {
        every { appConfig.iBaseUrl } returns "https://i.example.com"
        val expected = FollowingResponse()
        val expectedUrl = "https://i.example.com/api/v1/friendships/456/following/"
        coEvery { api.getFollowing(expectedUrl, null) } returns expected

        val result = repository.getFollowing("456", null)

        assertNotNull(result)
        assertEquals(expected, result)
        coVerify(exactly = 1) { api.getFollowing(expectedUrl, null) }
    }

    @Test
    fun `getStoryByUserId delegates to modern getStories with configured docIdStoryAndHighlight`() = runTest {
        val testDocId = "mock_story_doc_id"
        every { appConfig.docIdStoryAndHighlight } returns testDocId
        val userId = "user_99"
        val expected = UserStoryResponse()
        coEvery { api.getStories(testDocId, any()) } returns expected

        val result = repository.getStoryByUserId(userId)

        assertNotNull(result)
        assertEquals(expected, result)
        coVerify(exactly = 1) { api.getStories(testDocId, match { it.contains("user_99") }) }
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
    fun `getHighlightStoryByReelId appends highlight prefix and delegates to modern getStories`() = runTest {
        val testDocId = "mock_story_doc_id"
        every { appConfig.docIdStoryAndHighlight } returns testDocId
        val rawReelId = "12345"
        val expected = UserStoryResponse()
        coEvery { api.getStories(testDocId, any()) } returns expected

        val result = repository.getHighlightStoryByReelId(rawReelId)

        assertNotNull(result)
        assertEquals(expected, result)
        coVerify(exactly = 1) { api.getStories(testDocId, match { it.contains("highlight:12345") }) }
    }
}
