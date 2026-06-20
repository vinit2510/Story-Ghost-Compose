package `in`.vinit.storyghost.di

import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import `in`.vinit.storyghost.BuildConfig
import `in`.vinit.storyghost.data.local.DownloadsRepository
import `in`.vinit.storyghost.data.local.DownloadsRepositoryImpl
import `in`.vinit.storyghost.data.local.LocalDataSource
import `in`.vinit.storyghost.data.remote.auth.LoginInterceptor
import `in`.vinit.storyghost.data.remote.auth.LogoutAuthenticator
import `in`.vinit.storyghost.data.remote.config.FirebaseRemoteConfigImpl
import `in`.vinit.storyghost.data.remote.config.RemoteConfigRepository
import `in`.vinit.storyghost.data.remote.story.StoryAPI
import `in`.vinit.storyghost.data.remote.story.StoryRepository
import `in`.vinit.storyghost.data.remote.story.StoryRepositoryImpl
import `in`.vinit.storyghost.data.remote.user.UserAPI
import `in`.vinit.storyghost.data.remote.user.UserRepository
import `in`.vinit.storyghost.data.remote.user.UserRepositoryImpl
import `in`.vinit.storyghost.ui.dialog.LoginDialogViewModel
import `in`.vinit.storyghost.ui.dialog.LogoutDialogViewModel
import `in`.vinit.storyghost.ui.downloads.DownloadsViewModel
import `in`.vinit.storyghost.ui.home.HomeViewModel
import `in`.vinit.storyghost.ui.navigation.StartupViewModel
import `in`.vinit.storyghost.ui.onboarding.LoginViewModel
import `in`.vinit.storyghost.ui.onboarding.OnboardingViewModel
import `in`.vinit.storyghost.ui.story.HighlightViewModel
import `in`.vinit.storyghost.ui.story.StoryViewModel
import `in`.vinit.storyghost.util.AnalyticsLogger
import `in`.vinit.storyghost.util.FirebaseAnalyticsLogger
import `in`.vinit.storyghost.util.Logger
import `in`.vinit.storyghost.util.TimberLoggerImpl
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
    single<RemoteConfigRepository> {
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
            authenticator(LogoutAuthenticator(get()))
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
        DownloadsRepositoryImpl(androidContext())
    }

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
}
