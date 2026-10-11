package `in`.vinit.storyghost.data.remote.config

import `in`.vinit.storyghost.core.common.provider.AdConfigProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val configDataModule = module {
    single<RemoteConfigRepository>(createdAtStart = true) {
        FirebaseRemoteConfigImpl(androidContext(), get(), get())
    }

    single<AdConfigProvider> {
        get<RemoteConfigRepository>() as FirebaseRemoteConfigImpl
    }
}
