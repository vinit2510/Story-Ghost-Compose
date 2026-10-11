package `in`.vinit.storyghost.core.ads

import `in`.vinit.storyghost.core.common.AdInitializer
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val adsModule = module {
    single<AdInitializer> {
        AdInitializerImpl(androidContext(), get(), get(), get())
    }

    single { AdStateManager(get()) }
    single { AppOpenAdManager(get(), get(), get()) }
    single { InterstitialAdManager(androidContext(), get(), get(), get(), get()) }
}
