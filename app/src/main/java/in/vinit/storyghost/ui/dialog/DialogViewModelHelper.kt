package `in`.vinit.storyghost.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

@Composable
fun rememberViewModelStoreOwner(): ViewModelStoreOwner {
    val viewModelStore = remember { ViewModelStore() }
    DisposableEffect(Unit) {
        onDispose {
            viewModelStore.clear()
        }
    }
    return remember {
        object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore
                get() = viewModelStore
        }
    }
}
