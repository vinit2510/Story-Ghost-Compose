package `in`.vinit.storyghost.di

import `in`.vinit.storyghost.BuildConfig
import `in`.vinit.storyghost.MainViewModel
import `in`.vinit.storyghost.data.remote.StoryAPI
import `in`.vinit.storyghost.data.repository.StoryRepository
import `in`.vinit.storyghost.data.repository.StoryRepositoryImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    // Retrofit instance
    single {
        val client = OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
                )
            }
        }.build()

        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    // Story API
    single<StoryAPI> {
        get<Retrofit>().create(StoryAPI::class.java)
    }

    // Repository
    single<StoryRepository> {
        StoryRepositoryImpl(get())
    }

    // ViewModel
    viewModel {
        MainViewModel(get())
    }
}
