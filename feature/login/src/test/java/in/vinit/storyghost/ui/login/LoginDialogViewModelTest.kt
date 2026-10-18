package `in`.vinit.storyghost.ui.login

import `in`.vinit.storyghost.config.AppConfig
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.data.remote.config.RemoteConfigRepository
import `in`.vinit.storyghost.ui.UiState
import io.mockk.coEvery
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginDialogViewModelTest {

    private val remoteConfigRepository: RemoteConfigRepository = mockk(relaxed = true)
    private val appConfig: AppConfig = mockk(relaxed = true)
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

        viewModel = LoginDialogViewModel(remoteConfigRepository, appConfig, logger)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.cookieMinVersion.value
        assertTrue(state is UiState.Success)
        assertEquals(15, (state as UiState.Success).data)
    }

    @Test
    fun `loadCookieMinVersion failure sets UiState Error`() = runTest {
        coEvery { remoteConfigRepository.getCookieMinVersion() } throws RuntimeException("Remote config error")

        viewModel = LoginDialogViewModel(remoteConfigRepository, appConfig, logger)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.cookieMinVersion.value
        assertTrue(state is UiState.Error)
    }

    @Test
    fun `isCookieLoginEnabled returns true when versionCode is greater than or equal to minVersion`() {
        every { appConfig.versionCode } returns 20
        viewModel = LoginDialogViewModel(remoteConfigRepository, appConfig, logger)

        assertTrue(viewModel.isCookieLoginEnabled(15))
        assertTrue(viewModel.isCookieLoginEnabled(20))
    }

    @Test
    fun `isCookieLoginEnabled returns false when versionCode is less than minVersion`() {
        every { appConfig.versionCode } returns 10
        viewModel = LoginDialogViewModel(remoteConfigRepository, appConfig, logger)

        assertFalse(viewModel.isCookieLoginEnabled(15))
    }

    @Test
    fun `getOpenSourceContributionUrl returns empty string when not received from server`() {
        every { remoteConfigRepository.getOpenSourceContributionUrl() } returns ""

        viewModel = LoginDialogViewModel(remoteConfigRepository, appConfig, logger)

        assertEquals("", viewModel.getOpenSourceContributionUrl())
    }

    @Test
    fun `getOpenSourceContributionUrl returns url when received from server`() {
        every { remoteConfigRepository.getOpenSourceContributionUrl() } returns "https://github.com/vinit2510/Story-Ghost-Compose"

        viewModel = LoginDialogViewModel(remoteConfigRepository, appConfig, logger)

        assertEquals(
            "https://github.com/vinit2510/Story-Ghost-Compose",
            viewModel.getOpenSourceContributionUrl()
        )
    }
}
