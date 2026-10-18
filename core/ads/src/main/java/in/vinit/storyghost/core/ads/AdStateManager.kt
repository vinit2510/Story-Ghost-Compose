package `in`.vinit.storyghost.core.ads

import `in`.vinit.storyghost.core.common.storage.LocalStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdStateManager(private val localStorage: LocalStorage) {

    var isAnyAdShowing = false
        private set

    var isPremium = false
        private set

    init {
        CoroutineScope(Dispatchers.Default).launch {
            localStorage.isPremium().collect {
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
