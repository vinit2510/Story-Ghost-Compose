package `in`.vinit.storyghost.core.common

import android.content.Context
import com.facebook.ads.AudienceNetworkAds
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import com.google.firebase.FirebaseApp
import com.onesignal.OneSignal
import `in`.vinit.storyghost.BuildConfig
import `in`.vinit.storyghost.data.remote.config.RemoteConfigRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

class AppInitializer(
    private val context: Context,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val externalScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {
    fun initializeAll() {
        // 1. Timber Logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // 2. Firebase SDK
        FirebaseApp.initializeApp(context)

        // 3. OneSignal Push Notifications
        OneSignal.initWithContext(context, BuildConfig.ONESIGNAL_APP_ID)

        // 4. Facebook Audience Network
        AudienceNetworkAds.initialize(context)

        // 5. Next-Gen AdMob SDK (Background thread with SupervisorJob exception safety)
        externalScope.launch {
            val admobAppId = remoteConfigRepository.getAdmobAppId()
            MobileAds.initialize(
                context,
                InitializationConfig.Builder(admobAppId).build()
            ) {}
        }
    }
}
