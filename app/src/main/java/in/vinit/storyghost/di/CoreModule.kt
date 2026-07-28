package `in`.vinit.storyghost.di

import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import `in`.vinit.storyghost.core.analytics.AnalyticsLogger
import `in`.vinit.storyghost.core.analytics.FirebaseAnalyticsLogger
import `in`.vinit.storyghost.core.common.AppInitializer
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.core.common.TimberLoggerImpl
import `in`.vinit.storyghost.data.local.LocalDataSource
import `in`.vinit.storyghost.data.remote.config.FirebaseRemoteConfigImpl
import `in`.vinit.storyghost.data.remote.config.RemoteConfigRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

val coreModule: Module = module {
    single(createdAtStart = true) {
        AppInitializer(androidContext(), get()).apply { initializeAll() }
    }

    single<Logger> {
        TimberLoggerImpl()
    }

    single {
        PreferenceDataStoreFactory.create(
            produceFile = { androidContext().dataStoreFile("app_preferences.preferences_pb") },
            migrations = listOf(
                SharedPreferencesMigration(
                    context = androidContext(),
                    sharedPreferencesName = androidContext().packageName,
                    keysToMigrate = setOf(
                        LocalDataSource.Keys.IS_ONBOARDED.name,
                        LocalDataSource.Keys.IS_LOGGED.name,
                        LocalDataSource.Keys.COOKIE.name,
                        LocalDataSource.Keys.SESSION_ID.name,
                        LocalDataSource.Keys.CSRF_TOKEN.name,
                        LocalDataSource.Keys.USER_ID.name
                    )
                )
            )
        )
    }

    single {
        LocalDataSource(get())
    }

    single<RemoteConfigRepository>(createdAtStart = true) {
        FirebaseRemoteConfigImpl(androidContext(), get())
    }

    single<AnalyticsLogger> {
        FirebaseAnalyticsLogger()
    }
}
