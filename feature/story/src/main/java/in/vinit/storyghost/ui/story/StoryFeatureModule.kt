package `in`.vinit.storyghost.ui.story

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val storyFeatureModule = module {
    viewModelOf(::StoryViewModel)
}
