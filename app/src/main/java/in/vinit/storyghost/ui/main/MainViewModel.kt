package `in`.vinit.storyghost.ui.main

import androidx.lifecycle.ViewModel
import `in`.vinit.storyghost.data.local.LocalDataSource

class MainViewModel(
    private val localRepository: LocalDataSource
) : ViewModel() {
    // localRepository will be used here for onboarding and session routing checks
}