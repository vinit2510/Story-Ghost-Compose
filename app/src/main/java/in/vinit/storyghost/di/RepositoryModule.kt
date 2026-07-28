package `in`.vinit.storyghost.di

import `in`.vinit.storyghost.data.local.DownloadsRepository
import `in`.vinit.storyghost.data.local.DownloadsRepositoryImpl
import `in`.vinit.storyghost.data.remote.billing.BillingRepository
import `in`.vinit.storyghost.data.remote.billing.BillingRepositoryImpl
import `in`.vinit.storyghost.data.remote.story.StoryRepository
import `in`.vinit.storyghost.data.remote.story.StoryRepositoryImpl
import `in`.vinit.storyghost.data.remote.user.UserRepository
import `in`.vinit.storyghost.data.remote.user.UserRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

val repositoryModule: Module = module {
    single<StoryRepository> {
        StoryRepositoryImpl(get())
    }

    single<UserRepository> {
        UserRepositoryImpl(get())
    }

    single<DownloadsRepository> {
        DownloadsRepositoryImpl(androidContext(), get())
    }

    single<BillingRepository> {
        BillingRepositoryImpl(androidContext(), get(), get())
    }
}
