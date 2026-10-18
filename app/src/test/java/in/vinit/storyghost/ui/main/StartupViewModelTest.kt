package `in`.vinit.storyghost.ui.main

import `in`.vinit.storyghost.BuildConfig
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.data.local.LocalDataSource
import `in`.vinit.storyghost.data.remote.config.RemoteConfigRepository
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.navigation.Route
import io.mockk.coEvery
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
class StartupViewModelTest {

    private val localDataSource: LocalDataSource = mockk(relaxed = true)
    private val remoteConfigRepository: RemoteConfigRepository = mockk(relaxed = true)
    private val logger: Logger = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: StartupViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `forceUpdateVersion greater than current version routes to Route ForceUpdate`() = runTest {
        coEvery { remoteConfigRepository.getForceUpdateVersion() } returns BuildConfig.VERSION_CODE + 1

        viewModel = StartupViewModel(localDataSource, remoteConfigRepository, logger)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(Route.ForceUpdate, (state as UiState.Success).data)
    }

    @Test
    fun `onboarded user routes to Route Home`() = runTest {
        coEvery { remoteConfigRepository.getForceUpdateVersion() } returns BuildConfig.VERSION_CODE
        coEvery { localDataSource.isOnboarded() } returns flowOf(true)

        viewModel = StartupViewModel(localDataSource, remoteConfigRepository, logger)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(Route.Home, (state as UiState.Success).data)
    }

    @Test
    fun `non-onboarded user routes to Route Onboarding`() = runTest {
        coEvery { remoteConfigRepository.getForceUpdateVersion() } returns BuildConfig.VERSION_CODE
        coEvery { localDataSource.isOnboarded() } returns flowOf(false)

        viewModel = StartupViewModel(localDataSource, remoteConfigRepository, logger)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(Route.Onboarding, (state as UiState.Success).data)
    }
}
