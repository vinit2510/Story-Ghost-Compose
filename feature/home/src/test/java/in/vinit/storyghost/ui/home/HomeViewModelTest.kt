package `in`.vinit.storyghost.ui.home

import app.cash.turbine.test
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.core.common.storage.LocalStorage
import `in`.vinit.storyghost.data.billing.BillingRepository
import `in`.vinit.storyghost.data.model.FollowingResponse
import `in`.vinit.storyghost.data.model.SearchUserResponse
import `in`.vinit.storyghost.data.model.UserInfoResponse
import `in`.vinit.storyghost.data.story.StoryRepository
import `in`.vinit.storyghost.data.user.UserRepository
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import okhttp3.ResponseBody.Companion.toResponseBody

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val storyRepository: StoryRepository = mockk(relaxed = true)
    private val userRepository: UserRepository = mockk(relaxed = true)
    private val localStorage: LocalStorage = mockk(relaxed = true)
    private val billingRepository: BillingRepository = mockk(relaxed = true)
    private val logger: Logger = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { localStorage.getUserData() } returns flowOf(null)
        coEvery { localStorage.getUserId() } returns flowOf("user_123")
        coEvery { localStorage.isLoggedIn() } returns flowOf(true)
        coEvery { localStorage.isPremium() } returns flowOf(false)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): HomeViewModel {
        return HomeViewModel(
            storyRepository = storyRepository,
            userRepository = userRepository,
            localStorage = localStorage,
            billingRepository = billingRepository,
            logger = logger
        )
    }

    @Test
    fun `init checks subscription and loads following stories`() = runTest {
        val mockUsers = listOf(
            FollowingResponse.User(
                id = "story_user_1",
                username = "alice",
                latestReelMedia = 1700000000L
            ),
            FollowingResponse.User(
                id = "story_user_2",
                username = "bob",
                latestReelMedia = 0L // No active story
            )
        )
        val mockResponse = FollowingResponse(users = mockUsers, nextMaxId = null, hasMore = false)
        coEvery { storyRepository.getFollowing("user_123", null) } returns mockResponse

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { billingRepository.checkSubscription() }
        coVerify(atLeast = 1) { storyRepository.getFollowing("user_123", null) }

        viewModel.displayList.test {
            var state = awaitItem()
            if (state is UiState.Loading) {
                state = awaitItem()
            }
            assertTrue(state is UiState.Success)
            val list = (state as UiState.Success).data
            assertEquals(1, list.size)
            assertEquals("alice", list[0].node?.user?.username)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadStoryList triggers login dialog when userId is missing`() = runTest {
        coEvery { localStorage.getUserId() } returns flowOf(null)

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.showLoginDialogTrigger.value)
        viewModel.onLoginDialogTriggerHandled()
        assertFalse(viewModel.showLoginDialogTrigger.value)
    }

    @Test
    fun `loadStoryList 401 unauthorized triggers login dialog`() = runTest {
        val httpException =
            HttpException(Response.error<Any>(401, "".toResponseBody(null)))
        coEvery { storyRepository.getFollowing("user_123", any()) } throws httpException

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.showLoginDialogTrigger.value)
    }

    @Test
    fun `search filter works locally when remote search is not active`() = runTest {
        val mockUsers = listOf(
            FollowingResponse.User(id = "1", username = "charlie", latestReelMedia = 100L),
            FollowingResponse.User(id = "2", username = "david", latestReelMedia = 100L)
        )
        coEvery {
            storyRepository.getFollowing(
                "user_123",
                null
            )
        } returns FollowingResponse(users = mockUsers)

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChange("char")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.displayList.test {
            var state = awaitItem()
            if (state is UiState.Loading) {
                state = awaitItem()
            }
            assertTrue(state is UiState.Success)
            val list = (state as UiState.Success).data
            assertEquals(1, list.size)
            assertEquals("charlie", list[0].node?.user?.username)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `performSearch with query length at least threshold executes remote search`() = runTest {
        coEvery {
            storyRepository.getFollowing(
                "user_123",
                null
            )
        } returns FollowingResponse(users = emptyList())
        val searchResults = SearchUserResponse(
            users = listOf(
                SearchUserResponse.Data(
                    user = SearchUserResponse.Data.User(
                        id = "remote_id_1",
                        username = "remote_user"
                    )
                )
            )
        )
        coEvery { userRepository.searchUsersByQuery("remote") } returns searchResults

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChange("remote")
        viewModel.performSearch()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { userRepository.searchUsersByQuery("remote") }

        viewModel.displayList.test {
            var state = awaitItem()
            if (state is UiState.Loading) {
                state = awaitItem()
            }
            assertTrue(state is UiState.Success)
            val list = (state as UiState.Success).data
            assertEquals(1, list.size)
            assertEquals("remote_user", list[0].node?.user?.username)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleHighlightMode flips isHighlightMode flag`() = runTest {
        coEvery {
            storyRepository.getFollowing(
                "user_123",
                null
            )
        } returns FollowingResponse(users = emptyList())

        viewModel = createViewModel()
        assertFalse(viewModel.isHighlightMode.value)

        viewModel.toggleHighlightMode()
        assertTrue(viewModel.isHighlightMode.value)

        viewModel.toggleHighlightMode()
        assertFalse(viewModel.isHighlightMode.value)
    }

    @Test
    fun `logout clears authentication and resets states`() = runTest {
        coEvery {
            storyRepository.getFollowing(
                "user_123",
                null
            )
        } returns FollowingResponse(users = emptyList())

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.logout()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { localStorage.clearAuthentication() }
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `retry reloads story list`() = runTest {
        coEvery {
            storyRepository.getFollowing(
                "user_123",
                null
            )
        } returns FollowingResponse(users = emptyList())

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.retry()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(atLeast = 2) { storyRepository.getFollowing("user_123", null) }
    }

    @Test
    fun `reloadAfterLogin fetches missing user data and reloads stories`() = runTest {
        coEvery {
            storyRepository.getFollowing(
                "user_123",
                null
            )
        } returns FollowingResponse(users = emptyList())
        val mockUserInfo: UserInfoResponse = mockk(relaxed = true)
        coEvery { userRepository.getUserInfoByUserId("user_123") } returns mockUserInfo

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.reloadAfterLogin()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(atLeast = 1) { userRepository.getUserInfoByUserId("user_123") }
        coVerify(atLeast = 1) { localStorage.saveUserData(any()) }
    }

    @Test
    fun `loadNextPage appends paginated stories when hasMore is true`() = runTest {
        val firstPage = FollowingResponse(
            users = (1..6).map {
                FollowingResponse.User(
                    id = "user_$it",
                    username = "name_$it",
                    latestReelMedia = 100L
                )
            },
            nextMaxId = "next_cursor_1",
            hasMore = true
        )
        val secondPage = FollowingResponse(
            users = listOf(
                FollowingResponse.User(id = "user_7", username = "name_7", latestReelMedia = 100L)
            ),
            nextMaxId = null,
            hasMore = false
        )
        coEvery { storyRepository.getFollowing("user_123", null) } returns firstPage
        coEvery { storyRepository.getFollowing("user_123", "next_cursor_1") } returns secondPage

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.loadNextPage()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { storyRepository.getFollowing("user_123", "next_cursor_1") }
    }

    @Test
    fun `performRemoteSearch failure handles error and sets Error state`() = runTest {
        coEvery {
            storyRepository.getFollowing(
                "user_123",
                null
            )
        } returns FollowingResponse(users = emptyList())
        coEvery { userRepository.searchUsersByQuery("remote_fail") } throws RuntimeException("Network down")

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChange("remote_fail")
        viewModel.performSearch()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.displayList.test {
            var state = awaitItem()
            if (state is UiState.Loading) {
                state = awaitItem()
            }
            assertTrue(state is UiState.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
