package `in`.vinit.storyghost.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.vinit.storyghost.BuildConfig
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.data.local.LocalDataSource
import `in`.vinit.storyghost.data.remote.config.RemoteConfigRepository
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.navigation.Route
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val TAG = "StartupViewModel"

/**
 * Startup ViewModel responsible for calculating the initial navigation destination.
 *
 * Evaluation order:
 * 1. Checks [RemoteConfigRepository.getForceUpdateVersion] against [BuildConfig.VERSION_CODE].
 *    If a mandatory update is pending, routes to [Route.ForceUpdate].
 * 2. Checks [LocalDataSource.isOnboarded]. If false, routes to [Route.Onboarding].
 * 3. Otherwise routes to [Route.Home].
 */
class StartupViewModel(
    private val localRepository: LocalDataSource,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val logger: Logger
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Route>>(UiState.Loading)
    val uiState: StateFlow<UiState<Route>> = _uiState.asStateFlow()

    init {
        resolveInitialRoute()
    }

    private fun resolveInitialRoute() {
        logger.d(TAG, "Resolving initial route...")
        viewModelScope.launch {
            val forceUpdateVersion = remoteConfigRepository.getForceUpdateVersion()
            logger.d(
                TAG,
                "Force update version: $forceUpdateVersion, current: ${BuildConfig.VERSION_CODE}"
            )
            if (forceUpdateVersion > BuildConfig.VERSION_CODE) {
                logger.d(TAG, "Force update required. Routing to Route.ForceUpdate")
                _uiState.value = UiState.Success(Route.ForceUpdate)
                return@launch
            }

            val isOnboarded = try {
                localRepository.isOnboarded().first()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                logger.e(TAG, "Error reading isOnboarded preference from local storage", e)
                false
            }

            logger.d(TAG, "User is onboarded: $isOnboarded")
            val initialRoute = if (isOnboarded) {
                Route.Home
            } else {
                Route.Onboarding
            }
            logger.d(TAG, "Routing to $initialRoute")
            _uiState.value = UiState.Success(initialRoute)
        }
    }
}
