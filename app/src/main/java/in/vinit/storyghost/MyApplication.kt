package `in`.vinit.storyghost

import android.app.Application
import `in`.vinit.storyghost.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

/**
 * Main application class for Story Ghost.
 *
 * Responsible for initializing global singletons, starting Koin dependency injection,
 * and binding the application context across all modular components.
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(appModule)
        }
    }
}