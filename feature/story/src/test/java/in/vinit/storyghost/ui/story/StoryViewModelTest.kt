package `in`.vinit.storyghost.ui.story

import `in`.vinit.storyghost.core.ads.InterstitialAdManager
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.core.common.storage.LocalStorage
import `in`.vinit.storyghost.core.play.InAppReviewManager
import `in`.vinit.storyghost.data.downloads.DownloadsRepository
import `in`.vinit.storyghost.data.model.UserStoryResponse
import `in`.vinit.storyghost.data.model.UserStoryResponse.Data.ReelsMedia.Item
import `in`.vinit.storyghost.data.remote.config.RemoteConfigRepository
import `in`.vinit.storyghost.data.story.StoryRepository
import `in`.vinit.storyghost.ui.UiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StoryViewModelTest {

    private val storyRepository: StoryRepository = mockk(relaxed = true)
    private val downloadsRepository: DownloadsRepository = mockk(relaxed = true)
    private val localStorage: LocalStorage = mockk(relaxed = true)
    private val logger: Logger = mockk(relaxed = true)
    private val interstitialAdManager: InterstitialAdManager = mockk(relaxed = true)
    private val inAppReviewManager: InAppReviewManager = mockk(relaxed = true)
    private val remoteConfigRepository: RemoteConfigRepository = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: StoryViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { localStorage.isPremium() } returns flowOf(false)
        viewModel = StoryViewModel(
            storyRepository = storyRepository,
            downloadsRepository = downloadsRepository,
            localStorage = localStorage,
            logger = logger,
            interstitialAdManager = interstitialAdManager,
            inAppReviewManager = inAppReviewManager,
            remoteConfigRepository = remoteConfigRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadStories fetches user stories when called first time`() = runTest {
        val userId = "12345"
        val mockItems = listOf(
            Item(id = "item_1", mediaType = 1)
        )
        val mockResponse = UserStoryResponse(
            data = UserStoryResponse.Data(
                xdtReelsMedia = UserStoryResponse.Data.XdtReelsMedia(
                    reelsMedia = listOf(
                        UserStoryResponse.Data.ReelsMedia(
                            items = mockItems
                        )
                    )
                )
            )
        )
        coEvery { storyRepository.getStoryByUserId(userId) } returns mockResponse

        viewModel.loadStories(userId, isHighlight = false)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { storyRepository.getStoryByUserId(userId) }
        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(mockItems, (state as UiState.Success).data)
    }

    @Test
    fun `loadStories does not re-fetch when returning with same storyId and isHighlight`() =
        runTest {
            val userId = "12345"
            val mockItems = listOf(
                Item(id = "item_1", mediaType = 1)
            )
            val mockResponse = UserStoryResponse(
                data = UserStoryResponse.Data(
                    xdtReelsMedia = UserStoryResponse.Data.XdtReelsMedia(
                        reelsMedia = listOf(
                            UserStoryResponse.Data.ReelsMedia(
                                items = mockItems
                            )
                        )
                    )
                )
            )
            coEvery { storyRepository.getStoryByUserId(userId) } returns mockResponse

            // Initial load
            viewModel.loadStories(userId, isHighlight = false)
            testDispatcher.scheduler.advanceUntilIdle()
            coVerify(exactly = 1) { storyRepository.getStoryByUserId(userId) }

            // Second load (e.g. returning from HighlightScreen)
            viewModel.loadStories(userId, isHighlight = false)
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify no additional API call was made
            coVerify(exactly = 1) { storyRepository.getStoryByUserId(userId) }
            val state = viewModel.uiState.value
            assertTrue(state is UiState.Success)
            assertEquals(mockItems, (state as UiState.Success).data)
        }

    @Test
    fun `loadStories re-fetches when previous load resulted in error`() = runTest {
        val userId = "12345"
        val mockResponse = UserStoryResponse(
            data = UserStoryResponse.Data(
                xdtReelsMedia = UserStoryResponse.Data.XdtReelsMedia(
                    reelsMedia = listOf(
                        UserStoryResponse.Data.ReelsMedia(
                            items = emptyList()
                        )
                    )
                )
            )
        )
        // First attempt fails
        coEvery { storyRepository.getStoryByUserId(userId) } throws RuntimeException("Network error")

        viewModel.loadStories(userId, isHighlight = false)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify(exactly = 1) { storyRepository.getStoryByUserId(userId) }
        assertTrue(viewModel.uiState.value is UiState.Error)

        // Retry attempt succeeds
        coEvery { storyRepository.getStoryByUserId(userId) } returns mockResponse
        viewModel.loadStories(userId, isHighlight = false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify API was called again on retry
        coVerify(exactly = 2) { storyRepository.getStoryByUserId(userId) }
        assertTrue(viewModel.uiState.value is UiState.Success)
    }

    @Test
    fun `loadStories does not re-fetch highlight when called again with same highlightId`() =
        runTest {
            val highlightId = "highlight_999"
            val mockResponse = UserStoryResponse(
                data = UserStoryResponse.Data(
                    xdtReelsMedia = UserStoryResponse.Data.XdtReelsMedia(
                        reelsMedia = listOf(
                            UserStoryResponse.Data.ReelsMedia(
                                items = emptyList()
                            )
                        )
                    )
                )
            )
            coEvery { storyRepository.getHighlightStoryByReelId(highlightId) } returns mockResponse

            viewModel.loadStories(highlightId, isHighlight = true)
            testDispatcher.scheduler.advanceUntilIdle()
            coVerify(exactly = 1) { storyRepository.getHighlightStoryByReelId(highlightId) }

            // Call again
            viewModel.loadStories(highlightId, isHighlight = true)
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify still exactly 1
            coVerify(exactly = 1) { storyRepository.getHighlightStoryByReelId(highlightId) }
        }

    @Test
    fun `loadStories with isHighlight true reverses items to match chronological order`() =
        runTest {
            val highlightId = "highlight_999"
            val item1 = Item(id = "item_1", mediaType = 1)
            val item2 = Item(id = "item_2", mediaType = 2)
            val item3 = Item(id = "item_3", mediaType = 1)
            val mockResponse = UserStoryResponse(
                data = UserStoryResponse.Data(
                    xdtReelsMedia = UserStoryResponse.Data.XdtReelsMedia(
                        reelsMedia = listOf(
                            UserStoryResponse.Data.ReelsMedia(
                                items = listOf(item1, item2, item3)
                            )
                        )
                    )
                )
            )
            coEvery { storyRepository.getHighlightStoryByReelId(highlightId) } returns mockResponse

            viewModel.loadStories(highlightId, isHighlight = true)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is UiState.Success)
            assertEquals(listOf(item3, item2, item1), (state as UiState.Success).data)
        }

    @Test
    fun `loadAd calls interstitialAdManager loadAd`() {
        viewModel.loadAd()
        coVerify(exactly = 1) { interstitialAdManager.loadAd() }
    }

    @Test
    fun `showAd delegates to interstitialAdManager`() {
        val mockActivity: android.app.Activity = mockk(relaxed = true)
        viewModel.showAd(mockActivity)
        coVerify(exactly = 1) { interstitialAdManager.showAd(mockActivity, any()) }
    }

    @Test
    fun `showReviewDialogIfEligible calls inAppReviewManager`() = runTest {
        val mockActivity: android.app.Activity = mockk(relaxed = true)
        viewModel.showReviewDialogIfEligible(mockActivity)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify(exactly = 1) { inAppReviewManager.showDialogAfterDays(mockActivity) }
    }

    @Test
    fun `getInstagramAccountUrl returns url from remoteConfigRepository`() {
        io.mockk.every { remoteConfigRepository.getInstagramAccountUrl() } returns "https://instagram.com/test"
        assertEquals("https://instagram.com/test", viewModel.getInstagramAccountUrl())
    }

    @Test
    fun `downloadStory delegates to downloadsRepository`() {
        val item = Item(id = "item_1", mediaType = 1)
        val onSuccess: () -> Unit = {}
        viewModel.downloadStory(item, onSuccess)
        coVerify(exactly = 1) { downloadsRepository.downloadFile(item, onSuccess) }
    }

    @Test
    fun `loadHighlightStory failure updates uiState with error`() = runTest {
        val highlightId = "highlight_fail"
        coEvery { storyRepository.getHighlightStoryByReelId(highlightId) } throws RuntimeException("Network failure")

        viewModel.loadStories(highlightId, isHighlight = true)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is UiState.Error)
    }
}

