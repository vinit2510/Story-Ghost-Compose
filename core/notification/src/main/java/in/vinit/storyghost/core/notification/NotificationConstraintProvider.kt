package `in`.vinit.storyghost.core.notification

/**
 * Interface supplying application and user state for client-side push notification filtering.
 *
 * Implement this in host applications to evaluate targeted push campaigns
 * (app version ranges, free vs premium tiers, logged-in vs logged-out states).
 */
interface NotificationConstraintProvider {
    /** The host application's versionCode. */
    val currentVersionCode: Int

    /** Returns true if the current user has an active premium/paid subscription. */
    suspend fun isUserPremium(): Boolean = false

    /** Returns true if the user is currently authenticated/logged in. */
    suspend fun isUserLoggedIn(): Boolean = false
}

/**
 * Default fallback provider permitting all notifications.
 */
class DefaultNotificationConstraintProvider(
    override val currentVersionCode: Int = 0
) : NotificationConstraintProvider
