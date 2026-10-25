package `in`.vinit.storyghost.core.notification

import androidx.annotation.DrawableRes

/**
 * Runtime configuration for the push notification module.
 *
 * Allows host applications to customize default icons, topics, and channels
 * without modifying library code.
 */
data class NotificationConfig(
    @DrawableRes val defaultSmallIconRes: Int = R.drawable.ic_stat_notification,
    val defaultTopics: List<String> = listOf(DEFAULT_TOPIC_ALL),
    val defaultChannelId: String = NotificationChannels.CHANNEL_GENERAL_ID
) {

    companion object {
        const val DEFAULT_TOPIC_ALL = "all"
    }
}
