package `in`.vinit.storyghost.ui.login

import com.google.gson.Gson
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.core.common.storage.LocalStorage
import `in`.vinit.storyghost.data.model.UserInfoResponse
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LogoutDialogViewModelTest {

    private val localStorage: LocalStorage = mockk(relaxed = true)
    private val userRepository: UserRepository = mockk(relaxed = true)
    private val logger: Logger = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: LogoutDialogViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cached user JSON in localStorage loads successfully`() = runTest {
        val mockUser = UserInfoResponse(
            data = UserInfoResponse.Data(
                userContainer = UserInfoResponse.Data.UserContainer(
                    reel = UserInfoResponse.Data.UserContainer.Reel(
                        user = UserInfoResponse.Data.UserContainer.Reel.User(
                            id = "123",
                            username = "cached_user"
                        )
                    )
                )
            )
        )
        val userJson = Gson().toJson(mockUser)
        coEvery { localStorage.getUserData() } returns flowOf(userJson)

        viewModel = LogoutDialogViewModel(localStorage, userRepository, logger)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        val loadedUser = (state as UiState.Success).data
        assertEquals("cached_user", loadedUser?.data?.userContainer?.reel?.user?.username)
        coVerify(exactly = 0) { userRepository.getUserInfoByUserId(any()) }
    }

    @Test
    fun `missing cached user fetches from remote using userId and saves to localStorage`() =
        runTest {
            val mockUser = UserInfoResponse(
                data = UserInfoResponse.Data(
                    userContainer = UserInfoResponse.Data.UserContainer(
                        reel = UserInfoResponse.Data.UserContainer.Reel(
                            user = UserInfoResponse.Data.UserContainer.Reel.User(
                                id = "999",
                                username = "remote_user"
                            )
                        )
                    )
                )
            )
            coEvery { localStorage.getUserData() } returns flowOf(null)
            coEvery { localStorage.getUserId() } returns flowOf("999")
            coEvery { userRepository.getUserInfoByUserId("999") } returns mockUser

            viewModel = LogoutDialogViewModel(localStorage, userRepository, logger)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is UiState.Success)
            assertEquals(
                "remote_user",
                (state as UiState.Success).data?.data?.userContainer?.reel?.user?.username
            )
            coVerify(exactly = 1) { userRepository.getUserInfoByUserId("999") }
            coVerify(exactly = 1) { localStorage.saveUserData(any()) }
        }

    @Test
    fun `logout calls localStorage clearAuthentication`() = runTest {
        coEvery { localStorage.getUserData() } returns flowOf(null)
        coEvery { localStorage.getUserId() } returns flowOf(null)

        viewModel = LogoutDialogViewModel(localStorage, userRepository, logger)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.logout()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { localStorage.clearAuthentication() }
    }
}
