package `in`.vinit.storyghost.core.network.di

import `in`.vinit.storyghost.config.AppConfig
import `in`.vinit.storyghost.core.common.storage.LocalStorage
import `in`.vinit.storyghost.core.network.LoginInterceptor
import `in`.vinit.storyghost.core.network.MockInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single {
        val appConfig: AppConfig = get()
        val localStorage: LocalStorage = get()
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .followRedirects(false)
            .followSslRedirects(false)
            .addInterceptor(LoginInterceptor(localStorage, androidContext()))

        if (appConfig.isMock) {
            builder.addInterceptor(MockInterceptor(androidContext(), get()))
        }

        if (appConfig.isDebug) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            builder.addInterceptor(loggingInterceptor)
        }

        builder.build()
    }

    single {
        val appConfig: AppConfig = get()
        Retrofit.Builder()
            .baseUrl(appConfig.baseUrl)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
