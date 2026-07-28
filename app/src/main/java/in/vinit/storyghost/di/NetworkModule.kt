package `in`.vinit.storyghost.di

import `in`.vinit.storyghost.BuildConfig
import `in`.vinit.storyghost.core.network.MockInterceptor
import `in`.vinit.storyghost.data.remote.auth.LoginInterceptor
import `in`.vinit.storyghost.data.remote.story.StoryAPI
import `in`.vinit.storyghost.data.remote.user.UserAPI
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule: Module = module {
    single {
        val client = OkHttpClient.Builder().apply {
            addInterceptor(LoginInterceptor(get(), get()))
            if (BuildConfig.IS_MOCK) {
                addInterceptor(MockInterceptor(androidContext()))
            }
            followRedirects(false)
            followSslRedirects(false)
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
                )
            }
        }.build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    single {
        get<Retrofit>().create(StoryAPI::class.java)
    }

    single {
        get<Retrofit>().create(UserAPI::class.java)
    }
}
