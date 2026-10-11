package `in`.vinit.storyghost.ui.subscription

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val subscriptionFeatureModule = module {
    viewModelOf(::SubscriptionViewModel)
}
