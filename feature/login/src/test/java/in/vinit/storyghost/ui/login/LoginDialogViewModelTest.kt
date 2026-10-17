package `in`.vinit.storyghost.ui.login

import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.data.remote.config.RemoteConfigRepository
import `in`.vinit.storyghost.ui.UiState
import io.mockk.coEvery
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
class LoginDialogViewModelTest {

    private val remoteConfigRepository: RemoteConfigRepository = mockk(relaxed = true)
    private val logger: Logger = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: LoginDialogViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadCookieMinVersion success sets UiState Success`() = runTest {
        coEvery { remoteConfigRepository.getCookieMinVersion() } returns 15

        viewModel = LoginDialogViewModel(remoteConfigRepository, logger)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.cookieMinVersion.value
        assertTrue(state is UiState.Success)
        assertEquals(15, (state as UiState.Success).data)
    }

    @Test
    fun `loadCookieMinVersion failure sets UiState Error`() = runTest {
        coEvery { remoteConfigRepository.getCookieMinVersion() } throws RuntimeException("Remote config error")

        viewModel = LoginDialogViewModel(remoteConfigRepository, logger)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.cookieMinVersion.value
        assertTrue(state is UiState.Error)
    }
}
