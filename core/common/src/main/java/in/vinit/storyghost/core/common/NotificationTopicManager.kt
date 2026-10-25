package `in`.vinit.storyghost.core.common

/**
 * Manages client subscriptions to FCM push notification topics.
 */
interface NotificationTopicManager {
    /**
     * Subscribes this client to an FCM topic.
     *
     * @param topic Topic name to subscribe to.
     * @param onComplete Optional callback returning true if subscription succeeded.
     */
    fun subscribeToTopic(topic: String, onComplete: ((Boolean) -> Unit)? = null)

    /**
     * Unsubscribes this client from an FCM topic.
     *
     * @param topic Topic name to unsubscribe from.
     * @param onComplete Optional callback returning true if unsubscription succeeded.
     */
    fun unsubscribeFromTopic(topic: String, onComplete: ((Boolean) -> Unit)? = null)
}
