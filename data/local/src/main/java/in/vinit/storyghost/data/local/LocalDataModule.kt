package `in`.vinit.storyghost.data.local

import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import `in`.vinit.storyghost.core.common.storage.LocalStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val localDataModule = module {
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

    single<LocalStorage> {
        get<LocalDataSource>()
    }
}
