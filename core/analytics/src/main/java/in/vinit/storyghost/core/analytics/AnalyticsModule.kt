package `in`.vinit.storyghost.core.analytics

import org.koin.dsl.module

val analyticsModule = module {
    single<AnalyticsLogger> {
        FirebaseAnalyticsLogger()
    }
}
