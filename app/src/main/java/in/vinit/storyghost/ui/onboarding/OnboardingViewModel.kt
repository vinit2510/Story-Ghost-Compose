package `in`.vinit.storyghost.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.vinit.storyghost.data.local.LocalDataSource
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val localDataSource: LocalDataSource
) : ViewModel() {

    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            localDataSource.setOnboarded(true)
            onComplete()
        }
    }
}
