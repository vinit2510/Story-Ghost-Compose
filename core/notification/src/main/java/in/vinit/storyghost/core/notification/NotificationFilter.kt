package `in`.vinit.storyghost.core.notification

import `in`.vinit.storyghost.core.common.Logger
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Evaluates client-side constraints on the local device before displaying an FCM push.
 *
 * Enables targeted campaigns (e.g. Free-tier only, version-specific, auth-state specific)
 * without requiring custom backend filtering or tracking infrastructure.
 */
class NotificationFilter(
    private val constraintProvider: NotificationConstraintProvider,
    private val logger: Logger
) {

    /**
     * Returns true if the notification meets all client constraints and should be shown.
     * Returns false to drop the notification silently.
     */
    suspend fun shouldDisplay(constraints: NotificationConstraints): Boolean {
        val currentVersion = constraintProvider.currentVersionCode

        // 1. Min Version Check
        constraints.minVersionCode?.let { minVersion ->
            if (currentVersion < minVersion) {
                logger.d(
                    TAG,
                    "Dropped notification: app versionCode ($currentVersion) < minVersion ($minVersion)"
                )
                return false
            }
        }

        // 2. Max Version Check
        constraints.maxVersionCode?.let { maxVersion ->
            if (currentVersion > maxVersion) {
                logger.d(
                    TAG,
                    "Dropped notification: app versionCode ($currentVersion) > maxVersion ($maxVersion)"
                )
                return false
            }
        }

        // 3. User Tier Constraint (Premium vs Free)
        if (!constraints.targetTier.isNullOrBlank() && constraints.targetTier != NotificationConstraints.TIER_ALL) {
            val isPremium = withTimeoutOrNull(2000) {
                constraintProvider.isUserPremium()
            } ?: false

            when (constraints.targetTier.uppercase()) {
                NotificationConstraints.TIER_PREMIUM_ONLY -> {
                    if (!isPremium) {
                        logger.d(TAG, "Dropped notification: requires PREMIUM_ONLY, user is Free")
                        return false
                    }
                }
                NotificationConstraints.TIER_FREE_ONLY -> {
                    if (isPremium) {
                        logger.d(TAG, "Dropped notification: requires FREE_ONLY, user is Premium")
                        return false
                    }
                }
            }
        }

        // 4. Auth State Constraint (Logged In vs Logged Out)
        if (!constraints.targetAuth.isNullOrBlank() && constraints.targetAuth != NotificationConstraints.AUTH_ANY) {
            val isLoggedIn = withTimeoutOrNull(2000) {
                constraintProvider.isUserLoggedIn()
            } ?: false

            when (constraints.targetAuth.uppercase()) {
                NotificationConstraints.AUTH_LOGGED_IN_ONLY -> {
                    if (!isLoggedIn) {
                        logger.d(TAG, "Dropped notification: requires LOGGED_IN_ONLY, user is Logged Out")
                        return false
                    }
                }
                NotificationConstraints.AUTH_LOGGED_OUT_ONLY -> {
                    if (isLoggedIn) {
                        logger.d(TAG, "Dropped notification: requires LOGGED_OUT_ONLY, user is Logged In")
                        return false
                    }
                }
            }
        }

        return true
    }

    companion object {
        private const val TAG = "NotificationFilter"
    }
}
