package `in`.vinit.storyghost.ui.downloads

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val downloadsFeatureModule = module {
    viewModelOf(::DownloadsViewModel)
}
