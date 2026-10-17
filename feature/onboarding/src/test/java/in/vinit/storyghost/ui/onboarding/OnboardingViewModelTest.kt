package `in`.vinit.storyghost.ui.onboarding

import `in`.vinit.storyghost.config.AppConfig
import `in`.vinit.storyghost.core.common.storage.LocalStorage
import `in`.vinit.storyghost.data.remote.config.RemoteConfigRepository
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
class OnboardingViewModelTest {

    private val localStorage: LocalStorage = mockk(relaxed = true)
    private val remoteConfigRepository: RemoteConfigRepository = mockk(relaxed = true)
    private val appConfig: AppConfig = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = OnboardingViewModel(localStorage, remoteConfigRepository, appConfig)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `isCookieLoginEnabled returns true when versionCode is greater or equal to minVersion`() {
        every { appConfig.versionCode } returns 10
        every { remoteConfigRepository.getCookieMinVersion() } returns 10

        assertTrue(viewModel.isCookieLoginEnabled())

        every { appConfig.versionCode } returns 15
        every { remoteConfigRepository.getCookieMinVersion() } returns 10

        assertTrue(viewModel.isCookieLoginEnabled())
    }

    @Test
    fun `isCookieLoginEnabled returns false when versionCode is less than minVersion`() {
        every { appConfig.versionCode } returns 5
        every { remoteConfigRepository.getCookieMinVersion() } returns 10

        assertEquals(false, viewModel.isCookieLoginEnabled())
    }

    @Test
    fun `completeOnboarding marks onboarded in localStorage and invokes callback`() = runTest {
        var callbackCalled = false

        viewModel.completeOnboarding {
            callbackCalled = true
        }
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { localStorage.setOnboarded(true) }
        assertTrue(callbackCalled)
    }
}
