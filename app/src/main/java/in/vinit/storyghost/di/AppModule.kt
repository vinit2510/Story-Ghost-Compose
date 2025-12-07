package `in`.vinit.storyghost.di

import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import `in`.vinit.storyghost.BuildConfig
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
import `in`.vinit.storyghost.ui.main.MainViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

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
        FirebaseRemoteConfigImpl()
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

    // ViewModel
    viewModel {
        MainViewModel(get(), get(), get())
    }
}
