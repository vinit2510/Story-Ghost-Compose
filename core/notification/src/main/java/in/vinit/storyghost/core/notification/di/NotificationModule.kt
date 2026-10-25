package `in`.vinit.storyghost.core.notification.di

import `in`.vinit.storyghost.core.common.NotificationInitializer
import `in`.vinit.storyghost.core.common.NotificationTopicManager
import `in`.vinit.storyghost.core.notification.DefaultNotificationConstraintProvider
import `in`.vinit.storyghost.core.notification.NotificationBuilderHelper
import `in`.vinit.storyghost.core.notification.NotificationConfig
import `in`.vinit.storyghost.core.notification.NotificationConstraintProvider
import `in`.vinit.storyghost.core.notification.NotificationFilter
import `in`.vinit.storyghost.core.notification.NotificationImageLoader
import `in`.vinit.storyghost.core.notification.NotificationInitializerImpl
import `in`.vinit.storyghost.core.notification.NotificationTopicManagerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val notificationModule = module {
    single {
        NotificationConfig()
    }

    single {
        NotificationFilter(
            constraintProvider = getOrNull<NotificationConstraintProvider>() ?: DefaultNotificationConstraintProvider(),
            logger = get()
        )
    }

    single {
        NotificationImageLoader(
            logger = get()
        )
    }

    single {
        NotificationBuilderHelper(
            context = androidContext(),
            config = get()
        )
    }

    single<NotificationTopicManager> {
        NotificationTopicManagerImpl(
            logger = get()
        )
    }

    single<NotificationInitializer> {
        NotificationInitializerImpl(
            context = androidContext(),
            config = get(),
            topicManager = get(),
            logger = get()
        )
    }
}
