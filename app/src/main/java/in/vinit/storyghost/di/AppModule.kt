package `in`.vinit.storyghost.di

import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import `in`.vinit.storyghost.BuildConfig
import `in`.vinit.storyghost.core.ads.AdStateManager
import `in`.vinit.storyghost.core.ads.AppOpenAdManager
import `in`.vinit.storyghost.core.ads.InterstitialAdManager
import `in`.vinit.storyghost.core.analytics.AnalyticsLogger
import `in`.vinit.storyghost.core.analytics.FirebaseAnalyticsLogger
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.core.common.TimberLoggerImpl
import `in`.vinit.storyghost.core.play.InAppReviewManager
import `in`.vinit.storyghost.data.local.DownloadsRepository
import `in`.vinit.storyghost.data.local.DownloadsRepositoryImpl
import `in`.vinit.storyghost.data.local.LocalDataSource
import `in`.vinit.storyghost.data.remote.auth.LoginInterceptor
import `in`.vinit.storyghost.data.remote.billing.BillingRepository
import `in`.vinit.storyghost.data.remote.billing.BillingRepositoryImpl
import `in`.vinit.storyghost.data.remote.config.FirebaseRemoteConfigImpl
import `in`.vinit.storyghost.data.remote.config.RemoteConfigRepository
import `in`.vinit.storyghost.data.remote.story.StoryAPI
import `in`.vinit.storyghost.data.remote.story.StoryRepository
import `in`.vinit.storyghost.data.remote.story.StoryRepositoryImpl
import `in`.vinit.storyghost.data.remote.user.UserAPI
import `in`.vinit.storyghost.data.remote.user.UserRepository
import `in`.vinit.storyghost.data.remote.user.UserRepositoryImpl
import `in`.vinit.storyghost.ui.downloads.DownloadsViewModel
import `in`.vinit.storyghost.ui.highlight.HighlightViewModel
import `in`.vinit.storyghost.ui.home.HomeViewModel
import `in`.vinit.storyghost.ui.login.LoginDialogViewModel
import `in`.vinit.storyghost.ui.login.LoginViewModel
import `in`.vinit.storyghost.ui.login.LogoutDialogViewModel
import `in`.vinit.storyghost.ui.navigation.StartupViewModel
import `in`.vinit.storyghost.ui.onboarding.OnboardingViewModel
import `in`.vinit.storyghost.ui.story.StoryViewModel
import `in`.vinit.storyghost.ui.subscription.SubscriptionViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    // Logger
    single<Logger> {
        TimberLoggerImpl()
    }

    // DataStore
    single {
        PreferenceDataStoreFactory.create(
            produceFile = { androidContext().dataStoreFile("app_preferences.preferences_pb") }
        )
    }

    // LocalDataSource
    single {
        LocalDataSource(get())
    }

    // Remote config
    single<RemoteConfigRepository>(createdAtStart = true) {
        FirebaseRemoteConfigImpl(get())
    }

    // Analytics Helper
    single<AnalyticsLogger> {
        FirebaseAnalyticsLogger()
    }

    // Retrofit instance
    single {
        val client = OkHttpClient.Builder().apply {
            addInterceptor(LoginInterceptor(get(), get()))
            followRedirects(false)
            followSslRedirects(false)
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
                )
            }
        }.build()

        Retrofit.Builder()
            .baseUrl("https://api-base/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    // Story API
    single {
        get<Retrofit>().create(StoryAPI::class.java)
    }

    // User API
    single {
        get<Retrofit>().create(UserAPI::class.java)
    }

    // Story Repository
    single<StoryRepository> {
        StoryRepositoryImpl(get())
    }

    // User Repository
    single<UserRepository> {
        UserRepositoryImpl(get())
    }

    // Downloads Repository
    single<DownloadsRepository> {
        DownloadsRepositoryImpl(androidContext(), get())
    }

    // Billing Repository
    single<BillingRepository> {
        BillingRepositoryImpl(androidContext(), get(), get())
    }

    // Ad Managers
    single { AdStateManager(get()) }
    single { AppOpenAdManager(get(), get(), get()) }
    single { InterstitialAdManager(androidContext(), get(), get(), get(), get()) }
    single(createdAtStart = true) { InAppReviewManager(get(), get()) }

    // ViewModel
    viewModelOf(::StartupViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::LoginDialogViewModel)
    viewModelOf(::LogoutDialogViewModel)
    viewModelOf(::HighlightViewModel)
    viewModelOf(::StoryViewModel)
    viewModelOf(::DownloadsViewModel)
    viewModelOf(::SubscriptionViewModel)
}
