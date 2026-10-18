package `in`.vinit.storyghost.di

import `in`.vinit.storyghost.BuildConfig
import `in`.vinit.storyghost.config.AppConfig
import `in`.vinit.storyghost.core.ads.adsModule
import `in`.vinit.storyghost.core.analytics.analyticsModule
import `in`.vinit.storyghost.core.common.di.commonCoreModule
import `in`.vinit.storyghost.core.network.di.networkModule
import `in`.vinit.storyghost.core.play.di.playModule
import `in`.vinit.storyghost.data.billing.billingDataModule
import `in`.vinit.storyghost.data.downloads.downloadsDataModule
import `in`.vinit.storyghost.data.local.localDataModule
import `in`.vinit.storyghost.data.remote.config.configDataModule
import `in`.vinit.storyghost.data.story.storyDataModule
import `in`.vinit.storyghost.data.user.userDataModule
import `in`.vinit.storyghost.ui.downloads.downloadsFeatureModule
import `in`.vinit.storyghost.ui.highlight.highlightFeatureModule
import `in`.vinit.storyghost.ui.home.homeFeatureModule
import `in`.vinit.storyghost.ui.login.loginFeatureModule
import `in`.vinit.storyghost.ui.main.StartupViewModel
import `in`.vinit.storyghost.ui.onboarding.onboardingFeatureModule
import `in`.vinit.storyghost.ui.story.storyFeatureModule
import `in`.vinit.storyghost.ui.subscription.subscriptionFeatureModule
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Bridges Gradle `BuildConfig` variables into a decoupled, framework-free [AppConfig] data class.
 */
val appConfigModule = module {
    single {
        AppConfig(
            versionCode = BuildConfig.VERSION_CODE,
            isDebug = BuildConfig.DEBUG,
            applicationId = BuildConfig.APPLICATION_ID,
            baseUrl = BuildConfig.BASE_URL,
            iBaseUrl = BuildConfig.I_BASE_URL,
            isMock = BuildConfig.IS_MOCK,
            onesignalAppId = BuildConfig.ONESIGNAL_APP_ID,
            qhStoryList = BuildConfig.QH_STORY_LIST,
            qhUserStory = BuildConfig.QH_USER_STORY,
            qhHighlightList = BuildConfig.QH_HIGHLIGHT_LIST,
            qhHighlightStory = BuildConfig.QH_HIGHLIGHT_STORY,
            qhUserInfo = BuildConfig.QH_USER_INFO,
            fanRewardedInterstitialAdId = BuildConfig.FAN_REWARDED_INTERSTITIAL_AD_ID,
            admobRewardedInterstitialAdId = BuildConfig.ADMOB_REWARDED_INTERSTITIAL_AD_ID,
            admobInterstitialAdId = BuildConfig.ADMOB_INTERSTITIAL_AD_ID,
            admobAppOpenAdId = BuildConfig.ADMOB_APP_OPEN_AD_ID,
            admobAppId = BuildConfig.ADMOB_APP_ID
        )
    }
}

val appViewModelModule = module {
    viewModelOf(::StartupViewModel)
}

/**
 * Root Koin module consolidating all Core, Data, and Feature dependency injection graphs.
 */
val appModule = module {
    includes(
        appConfigModule,
        commonCoreModule,
        adsModule,
        analyticsModule,
        networkModule,
        playModule,
        localDataModule,
        configDataModule,
        userDataModule,
        storyDataModule,
        billingDataModule,
        downloadsDataModule,
        homeFeatureModule,
        storyFeatureModule,
        highlightFeatureModule,
        loginFeatureModule,
        onboardingFeatureModule,
        downloadsFeatureModule,
        subscriptionFeatureModule,
        appViewModelModule
    )
}
