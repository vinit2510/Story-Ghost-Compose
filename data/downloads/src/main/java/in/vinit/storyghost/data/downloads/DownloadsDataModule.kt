package `in`.vinit.storyghost.data.downloads

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val downloadsDataModule = module {
    single<DownloadsRepository> {
        DownloadsRepositoryImpl(androidContext(), get())
    }
}
