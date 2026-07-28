package `in`.vinit.storyghost

import android.app.Application
import com.facebook.ads.AudienceNetworkAds
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import com.google.firebase.FirebaseApp
import com.onesignal.OneSignal
import `in`.vinit.storyghost.data.remote.config.RemoteConfigRepository
import `in`.vinit.storyghost.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import timber.log.Timber

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(appModule)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        FirebaseApp.initializeApp(this)

        // Initialize OneSignal
        OneSignal.initWithContext(this, BuildConfig.ONESIGNAL_APP_ID)

        // Initialize Facebook Audience Network
        AudienceNetworkAds.initialize(this)

        // Initialize Next-Gen AdMob SDK on a background thread
        CoroutineScope(Dispatchers.IO).launch {
            val remoteConfigRepository = get<RemoteConfigRepository>()
            val admobAppId = remoteConfigRepository.getAdmobAppId()
            MobileAds.initialize(
                this@MyApplication,
                InitializationConfig.Builder(admobAppId).build()
            ) {}
        }

    }
}