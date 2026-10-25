package `in`.vinit.storyghost.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import `in`.vinit.storyghost.ui.navigation.Route
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import `in`.vinit.storyghost.core.ads.AppOpenAdManager
import `in`.vinit.storyghost.core.analytics.LocalAnalyticsLogger
import `in`.vinit.storyghost.core.play.InAppUpdateManager
import `in`.vinit.storyghost.ui.theme.StoryGhostTheme
import org.koin.android.ext.android.inject
import org.koin.compose.koinInject

/**
 * Single-activity host for the Story Ghost Compose UI.
 *
 * Responsibilities:
 * - Enables Android edge-to-edge drawing for immersive UI.
 * - Manages process lifecycle observation for Google Mobile Ads [AppOpenAdManager].
 * - Checks for flexible Google Play in-app updates via [InAppUpdateManager].
 * - Sets the root Jetpack Compose content with [StoryGhostTheme] and [LocalAnalyticsLogger].
 */
class MainActivity : ComponentActivity() {

    private val appOpenAdManager: AppOpenAdManager by inject()
    private val inAppUpdateManager: InAppUpdateManager by inject()
    private val deepLinkRoute = mutableStateOf<Route?>(null)

    private val updateLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ProcessLifecycleOwner.get().lifecycle.addObserver(processLifecycleObserver)

        // Check for Google Play in-app updates
        inAppUpdateManager.checkForUpdate(this, updateLauncher)

        handleNotificationIntent(intent)

        setContent {
            CompositionLocalProvider(LocalAnalyticsLogger provides koinInject()) {
                StoryGhostTheme {
                    App(
                        modifier = Modifier.fillMaxSize(),
                        deepLinkRoute = deepLinkRoute.value,
                        onDeepLinkConsumed = { deepLinkRoute.value = null }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        val resolvedRoute = Route.fromDeepLink(intent?.dataString)
        if (resolvedRoute != null) {
            deepLinkRoute.value = resolvedRoute
        }
    }

    private val processLifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            appOpenAdManager.showAd(this@MainActivity)
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            appOpenAdManager.loadAd()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ProcessLifecycleOwner.get().lifecycle.removeObserver(processLifecycleObserver)
    }
}