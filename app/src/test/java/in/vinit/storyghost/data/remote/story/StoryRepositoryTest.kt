package `in`.vinit.storyghost.data.remote.story

import `in`.vinit.storyghost.data.model.HighlightsResponse
import `in`.vinit.storyghost.data.model.StoryListResponse
import `in`.vinit.storyghost.data.model.UserStoryResponse
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
    fun `getStoryList delegates to API with expected params`() = runTest {
        val expected = StoryListResponse()
        coEvery { api.getStoryList(any(), any()) } returns expected

        val result = repository.getStoryList()

        assertNotNull(result)
        assertEquals(expected, result)
        coVerify(exactly = 1) { api.getStoryList(any(), any()) }
    }

    @Test
    fun `getUserStory delegates to API with user ID`() = runTest {
        val userId = "user_99"
        val expected = UserStoryResponse()
        coEvery { api.getUserStory(any(), any()) } returns expected

        val result = repository.getUserStory(userId)

        assertNotNull(result)
        assertEquals(expected, result)
        coVerify(exactly = 1) { api.getUserStory(any(), any()) }
    }

    @Test
    fun `getHighlights delegates to API with user ID`() = runTest {
        val userId = "user_99"
        val expected = HighlightsResponse()
        coEvery { api.getHighlights(any(), any()) } returns expected

        val result = repository.getHighlights(userId)

        assertNotNull(result)
        assertEquals(expected, result)
        coVerify(exactly = 1) { api.getHighlights(any(), any()) }
    }

    @Test
    fun `getUserHighlights delegates to API with highlight reel ID`() = runTest {
        val reelId = "highlight_123"
        val expected = UserStoryResponse()
        coEvery { api.getUserHighlights(any(), any()) } returns expected

        val result = repository.getUserHighlights(reelId)

        assertNotNull(result)
        assertEquals(expected, result)
        coVerify(exactly = 1) { api.getUserHighlights(any(), any()) }
    }
}
