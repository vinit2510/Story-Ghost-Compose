package `in`.vinit.storyghost.core.common

import android.content.Context
import com.onesignal.OneSignal
import `in`.vinit.storyghost.config.AppConfig
import timber.log.Timber

class AppInitializer(
    private val context: Context,
    private val appConfig: AppConfig,
    private val firebaseInitializer: FirebaseInitializer,
    private val adInitializer: AdInitializer,
    private val logger: Logger
) {
    fun initializeAll() {
        initTimber()
        initFirebase()
        initOneSignal()
        initAds()
    }

    private fun initTimber() {
        if (appConfig.isDebug) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initFirebase() {
        firebaseInitializer.initialize()
    }

    private fun initOneSignal() {
        try {
            OneSignal.initWithContext(context, appConfig.onesignalAppId)
        } catch (e: Exception) {
            logger.e("AppInitializer", "Failed to initialize OneSignal", e)
        }
    }

    private fun initAds() {
        adInitializer.initialize()
    }
}
