package `in`.vinit.storyghost.core.common.di

import `in`.vinit.storyghost.core.common.AppInitializer
import `in`.vinit.storyghost.core.common.FirebaseInitializer
import `in`.vinit.storyghost.core.common.FirebaseInitializerImpl
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.core.common.TimberLoggerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val commonCoreModule = module {
    single<Logger> {
        TimberLoggerImpl()
    }

    single<FirebaseInitializer> {
        FirebaseInitializerImpl(androidContext(), get())
    }

    single(createdAtStart = true) {
        AppInitializer(androidContext(), get(), get(), get(), get()).apply { initializeAll() }
    }
}
