package `in`.vinit.storyghost.core.notification

import android.content.Context
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.core.common.NotificationInitializer
import `in`.vinit.storyghost.core.common.NotificationTopicManager

class NotificationInitializerImpl(
    private val context: Context,
    private val config: NotificationConfig,
    private val topicManager: NotificationTopicManager,
    private val logger: Logger
) : NotificationInitializer {

    override fun initialize() {
        try {
            NotificationChannels.createNotificationChannels(context)
            config.defaultTopics.forEach { topic ->
                topicManager.subscribeToTopic(topic)
            }
        } catch (e: Exception) {
            logger.e(TAG, "Failed to initialize notifications", e)
        }
    }

    companion object {
        private const val TAG = "NotificationInitializer"
    }
}
