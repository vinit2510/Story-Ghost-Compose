package `in`.vinit.storyghost.ui.highlight

import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.data.model.HighlightsResponse
import `in`.vinit.storyghost.data.model.HighlightsResponse.Data.User.EdgeHighlightReels.Edge
import `in`.vinit.storyghost.data.story.StoryRepository
import `in`.vinit.storyghost.ui.UiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class HighlightViewModelTest {

    private val storyRepository: StoryRepository = mockk(relaxed = true)
    private val logger: Logger = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HighlightViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = HighlightViewModel(storyRepository, logger)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() {
        assertTrue(viewModel.uiState.value is UiState.Loading)
    }

    @Test
    fun `loadHighlights success updates uiState to Success with edges`() = runTest {
        val mockEdge = Edge(
            node = Edge.Node(
                id = "highlight_123",
                title = "Summer Vacation"
            )
        )
        val mockResponse = HighlightsResponse(
            data = HighlightsResponse.Data(
                user = HighlightsResponse.Data.User(
                    edgeHighlightReels = HighlightsResponse.Data.User.EdgeHighlightReels(
                        edges = listOf(mockEdge)
                    )
                )
            )
        )
        coEvery { storyRepository.getHighlightReelsByUserId("user_1") } returns mockResponse

        viewModel.loadHighlights("user_1")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        val data = (state as UiState.Success).data
        assertEquals(1, data.size)
        assertEquals("highlight_123", data[0].node?.id)
        assertEquals("Summer Vacation", data[0].node?.title)
    }

    @Test
    fun `loadHighlights does not re-fetch if state is already Success`() = runTest {
        val mockResponse = HighlightsResponse(
            data = HighlightsResponse.Data(
                user = HighlightsResponse.Data.User(
                    edgeHighlightReels = HighlightsResponse.Data.User.EdgeHighlightReels(
                        edges = emptyList()
                    )
                )
            )
        )
        coEvery { storyRepository.getHighlightReelsByUserId("user_1") } returns mockResponse

        viewModel.loadHighlights("user_1")
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value is UiState.Success)

        viewModel.loadHighlights("user_1")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { storyRepository.getHighlightReelsByUserId("user_1") }
    }

    @Test
    fun `loadHighlights failure updates uiState to Error and logs`() = runTest {
        val exception = IOException("Network failure")
        coEvery { storyRepository.getHighlightReelsByUserId("user_1") } throws exception

        viewModel.loadHighlights("user_1")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Error)
        coVerify(exactly = 1) {
            logger.e(
                "HighlightViewModel",
                "loadHighlights: Failed to load highlights for user user_1",
                any()
            )
        }
    }
}
