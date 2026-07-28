package `in`.vinit.storyghost.ui.login

import app.cash.turbine.test
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.data.local.LocalDataSource
import `in`.vinit.storyghost.data.remote.config.RemoteConfigRepository
import `in`.vinit.storyghost.ui.UiState
import io.mockk.coVerify
import io.mockk.every
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

    private val localDataSource: LocalDataSource = mockk(relaxed = true)
    private val remoteConfigRepository: RemoteConfigRepository = mockk(relaxed = true)
    private val logger: Logger = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(localDataSource, remoteConfigRepository, logger)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loginWithCookie with invalid cookie sets UiState Error`() = runTest {
        val invalidCookie = "some_invalid_cookie_without_ids"

        viewModel.loginWithCookie(invalidCookie)

        assertTrue(viewModel.uiState.value is UiState.Error)
    }

    @Test
    fun `loginWithCookie with valid cookie saves session and sets UiState Success`() = runTest {
        val validCookie = "sessionid=12345; ds_user_id=67890; csrftoken=abc"

        viewModel.uiState.test {
            assertEquals(UiState.Success(false), awaitItem())

            viewModel.loginWithCookie(validCookie)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(UiState.Loading, awaitItem())
            assertEquals(UiState.Success(true), awaitItem())
        }

        coVerify { localDataSource.setLoggedIn(true) }
        coVerify { localDataSource.setOnboarded(true) }
    }

    @Test
    fun `resetUiState sets state back to Success false`() {
        viewModel.resetUiState()
        assertEquals(UiState.Success(false), viewModel.uiState.value)
    }

    @Test
    fun `getInstagramLoginUrl delegates to remoteConfigRepository`() {
        val expectedUrl = "https://www.instagram.com/accounts/login/"
        every { remoteConfigRepository.getInstagramLoginUrl() } returns expectedUrl

        val result = viewModel.getInstagramLoginUrl()
        assertEquals(expectedUrl, result)
    }
}
