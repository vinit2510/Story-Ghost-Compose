package `in`.vinit.storyghost.core.notification

import com.google.firebase.messaging.FirebaseMessaging
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.core.common.NotificationTopicManager

class NotificationTopicManagerImpl(
    private val logger: Logger
) : NotificationTopicManager {

    override fun subscribeToTopic(topic: String, onComplete: ((Boolean) -> Unit)?) {
        if (topic.isBlank()) {
            onComplete?.invoke(false)
            return
        }
        try {
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener { task ->
                    val isSuccess = task.isSuccessful
                    if (isSuccess) {
                        logger.d(TAG, "Subscribed to FCM topic: $topic")
                    } else {
                        logger.w(TAG, "Failed to subscribe to topic: $topic: ${task.exception?.message}")
                    }
                    onComplete?.invoke(isSuccess)
                }
        } catch (e: Exception) {
            logger.w(TAG, "Failed to invoke subscribeToTopic: ${e.message}", e)
            onComplete?.invoke(false)
        }
    }

    override fun unsubscribeFromTopic(topic: String, onComplete: ((Boolean) -> Unit)?) {
        if (topic.isBlank()) {
            onComplete?.invoke(false)
            return
        }
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener { task ->
                    val isSuccess = task.isSuccessful
                    if (isSuccess) {
                        logger.d(TAG, "Unsubscribed from FCM topic: $topic")
                    } else {
                        logger.w(TAG, "Failed to unsubscribe from topic: $topic: ${task.exception?.message}")
                    }
                    onComplete?.invoke(isSuccess)
                }
        } catch (e: Exception) {
            logger.w(TAG, "Failed to invoke unsubscribeFromTopic: ${e.message}", e)
            onComplete?.invoke(false)
        }
    }

    companion object {
        private const val TAG = "NotificationTopicManager"
    }
}
