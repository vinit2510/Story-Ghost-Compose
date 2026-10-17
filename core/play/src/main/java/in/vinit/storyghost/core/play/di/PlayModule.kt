package `in`.vinit.storyghost.core.play.di

import `in`.vinit.storyghost.core.play.InAppReviewManager
import `in`.vinit.storyghost.core.play.InAppUpdateManager
import org.koin.dsl.module

val playModule = module {
    single(createdAtStart = true) {
        InAppReviewManager(get(), get())
    }

    single {
        InAppUpdateManager(get())
    }
}
