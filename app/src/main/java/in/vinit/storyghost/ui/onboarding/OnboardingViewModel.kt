package `in`.vinit.storyghost.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.vinit.storyghost.BuildConfig
import `in`.vinit.storyghost.data.local.LocalDataSource
import `in`.vinit.storyghost.data.remote.config.RemoteConfigRepository
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val localDataSource: LocalDataSource,
    private val remoteConfigRepository: RemoteConfigRepository
) : ViewModel() {

    fun isCookieLoginEnabled(): Boolean {
        return BuildConfig.VERSION_CODE >= remoteConfigRepository.getCookieMinVersion()
    }

    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            localDataSource.setOnboarded(true)
            onComplete()
        }
    }
}
