package `in`.vinit.storyghost.util

import `in`.vinit.storyghost.data.local.LocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdStateManager(private val localDataSource: LocalDataSource) {

    var isAnyAdShowing = false
        private set

    var isPremium = false
        private set

    init {
        CoroutineScope(Dispatchers.Default).launch {
            localDataSource.isPremium().collect {
                isPremium = it
            }
        }
    }

    fun onAdShown() {
        isAnyAdShowing = true
    }

    fun onAdDismissed() {
        isAnyAdShowing = false
    }
}
