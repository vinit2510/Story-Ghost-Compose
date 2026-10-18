package `in`.vinit.storyghost.data.story

import `in`.vinit.storyghost.data.remote.story.StoryAPI
import org.koin.dsl.module
import retrofit2.Retrofit

val storyDataModule = module {
    single {
        get<Retrofit>().create(StoryAPI::class.java)
    }

    single<StoryRepository> {
        StoryRepositoryImpl(get(), get())
    }
}
