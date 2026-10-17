package `in`.vinit.storyghost.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.vinit.storyghost.config.AppConfig
import `in`.vinit.storyghost.core.common.storage.LocalStorage
import `in`.vinit.storyghost.data.remote.config.RemoteConfigRepository
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val localStorage: LocalStorage,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val appConfig: AppConfig
) : ViewModel() {

    fun isCookieLoginEnabled(): Boolean {
        return appConfig.versionCode >= remoteConfigRepository.getCookieMinVersion()
    }

    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            localStorage.setOnboarded(true)
            onComplete()
        }
    }
}
