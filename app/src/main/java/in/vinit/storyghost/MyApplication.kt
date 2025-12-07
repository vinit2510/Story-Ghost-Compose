package `in`.vinit.storyghost

import android.app.Application
import com.google.firebase.FirebaseApp
import `in`.vinit.storyghost.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import timber.log.Timber

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        FirebaseApp.initializeApp(this)

        startKoin {
            androidContext(this@MyApplication)
            modules(appModule)
        }

    }
}