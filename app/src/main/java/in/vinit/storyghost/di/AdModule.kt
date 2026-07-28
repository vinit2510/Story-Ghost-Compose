package `in`.vinit.storyghost.di

import `in`.vinit.storyghost.core.ads.AdStateManager
import `in`.vinit.storyghost.core.ads.AppOpenAdManager
import `in`.vinit.storyghost.core.ads.InterstitialAdManager
import `in`.vinit.storyghost.core.play.InAppReviewManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

val adModule: Module = module {
    single { AdStateManager(get()) }
    single { AppOpenAdManager(get(), get(), get()) }
    single { InterstitialAdManager(androidContext(), get(), get(), get(), get()) }
    single(createdAtStart = true) { InAppReviewManager(get(), get()) }
}
