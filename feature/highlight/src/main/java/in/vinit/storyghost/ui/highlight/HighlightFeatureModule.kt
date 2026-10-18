package `in`.vinit.storyghost.ui.highlight

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val highlightFeatureModule = module {
    viewModelOf(::HighlightViewModel)
}
