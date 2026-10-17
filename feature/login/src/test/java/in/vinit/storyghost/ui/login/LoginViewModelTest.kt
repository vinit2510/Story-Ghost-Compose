package `in`.vinit.storyghost.ui.login

import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.core.common.storage.LocalStorage
import `in`.vinit.storyghost.data.remote.config.RemoteConfigRepository
import `in`.vinit.storyghost.ui.UiState
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

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val localStorage: LocalStorage = mockk(relaxed = true)
    private val remoteConfigRepository: RemoteConfigRepository = mockk(relaxed = true)
    private val logger: Logger = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(localStorage, remoteConfigRepository, logger)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loginWithCookie parses sessionid and ds_user_id then saves to localStorage`() = runTest {
        val validCookie = "sessionid=123456%3Aabc; ds_user_id=98765; csrftoken=xyz"

        viewModel.loginWithCookie(validCookie)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { localStorage.saveCookie(validCookie) }
        coVerify(exactly = 1) { localStorage.saveSessionId("123456%3Aabc") }
        coVerify(exactly = 1) { localStorage.saveUserId("98765") }
        coVerify(exactly = 1) { localStorage.setLoggedIn(true) }
        coVerify(exactly = 1) { localStorage.setOnboarded(true) }

        val currentState = viewModel.uiState.value
        assertTrue(currentState is UiState.Success)
        assertEquals(true, (currentState as UiState.Success).data)
    }

    @Test
    fun `loginWithCookie fails when required cookie keys are missing`() = runTest {
        val invalidCookie = "some_other_cookie=value"

        viewModel.loginWithCookie(invalidCookie)
        testDispatcher.scheduler.advanceUntilIdle()

        val currentState = viewModel.uiState.value
        assertTrue(currentState is UiState.Error)
    }
}
