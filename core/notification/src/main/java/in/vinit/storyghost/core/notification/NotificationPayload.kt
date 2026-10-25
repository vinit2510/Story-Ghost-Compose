package `in`.vinit.storyghost.core.notification

/**
 * Models the parsed industry-standard FCM data-only notification payload.
 *
 * All fields are deserialized from `remoteMessage.data` (which are strictly string key-value pairs).
 * Uses a single strict schema without redundant fallback keys.
 *
 * ### FCM HTTP v1 JSON Payload Example:
 * ```json
 * {
 *   "message": {
 *     "topic": "all",
 *     "data": {
 *       "title": "🎉 Special Weekend Deal!",
 *       "body": "Unlock full access to StoryGhost today with 50% discount.",
 *       "channel_id": "promotions",
 *       "launch_url": "storyghost://subscription",
 *       "image_url": "https://example.com/banner.jpg",
 *       "icon_url": "https://example.com/avatar.png",
 *       "btn1_text": "Claim Offer",
 *       "btn1_url": "storyghost://subscription",
 *       "btn2_text": "Later",
 *       "btn2_url": "storyghost://home",
 *       "constraint_min_version": "10",
 *       "constraint_max_version": "50",
 *       "constraint_tier": "FREE_ONLY",
 *       "constraint_auth": "LOGGED_IN_ONLY"
 *     }
 *   }
 * }
 * ```
 */
data class NotificationPayload(
    val title: String,
    val body: String,
    val channelId: String = NotificationChannels.CHANNEL_GENERAL_ID,
    val launchUrl: String? = null,
    val imageUrl: String? = null,
    val iconUrl: String? = null,
    val actions: List<NotificationAction> = emptyList(),
    val constraints: NotificationConstraints = NotificationConstraints()
) {
    companion object {
        fun fromData(data: Map<String, String>): NotificationPayload {
            val title = data["title"].orEmpty()
            val body = data["body"].orEmpty()
            val channelId = data["channel_id"]?.takeIf { it.isNotBlank() } ?: NotificationChannels.CHANNEL_GENERAL_ID
            val launchUrl = data["launch_url"]?.takeIf { it.isNotBlank() }
            val imageUrl = data["image_url"]?.takeIf { it.isNotBlank() }
            val iconUrl = data["icon_url"]?.takeIf { it.isNotBlank() }

            val actions = buildList {
                val btn1Text = data["btn1_text"]?.takeIf { it.isNotBlank() }
                if (btn1Text != null) {
                    add(NotificationAction(id = "btn_1", title = btn1Text, url = data["btn1_url"]?.takeIf { it.isNotBlank() }))
                }

                val btn2Text = data["btn2_text"]?.takeIf { it.isNotBlank() }
                if (btn2Text != null) {
                    add(NotificationAction(id = "btn_2", title = btn2Text, url = data["btn2_url"]?.takeIf { it.isNotBlank() }))
                }
            }

            val constraints = NotificationConstraints(
                minVersionCode = data["constraint_min_version"]?.toIntOrNull(),
                maxVersionCode = data["constraint_max_version"]?.toIntOrNull(),
                targetTier = data["constraint_tier"]?.takeIf { it.isNotBlank() },
                targetAuth = data["constraint_auth"]?.takeIf { it.isNotBlank() }
            )

            return NotificationPayload(
                title = title,
                body = body,
                channelId = channelId,
                launchUrl = launchUrl,
                imageUrl = imageUrl,
                iconUrl = iconUrl,
                actions = actions,
                constraints = constraints
            )
        }
    }
}

/**
 * Clickable action button inside the notification.
 */
data class NotificationAction(
    val id: String,
    val title: String,
    val url: String? = null
)

/**
 * On-device client constraints evaluated before displaying the notification.
 */
data class NotificationConstraints(
    val minVersionCode: Int? = null,
    val maxVersionCode: Int? = null,
    val targetTier: String? = null,
    val targetAuth: String? = null
) {
    companion object {
        const val TIER_ALL = "ALL"
        const val TIER_PREMIUM_ONLY = "PREMIUM_ONLY"
        const val TIER_FREE_ONLY = "FREE_ONLY"

        const val AUTH_ANY = "ANY"
        const val AUTH_LOGGED_IN_ONLY = "LOGGED_IN_ONLY"
        const val AUTH_LOGGED_OUT_ONLY = "LOGGED_OUT_ONLY"
    }
}
