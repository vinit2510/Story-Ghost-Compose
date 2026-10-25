package `in`.vinit.storyghost.ui.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import `in`.vinit.storyghost.core.analytics.AnalyticsScreen
import kotlinx.serialization.Serializable
import androidx.core.net.toUri

/**
 * Sealed interface representing type-safe navigation destinations for the app.
 *
 * Implements [NavKey] for compatibility with AndroidX Navigation 3 and provides
 * an [analyticsScreenName] for automatic screen view event tracking.
 */
@Immutable
@Serializable
sealed interface Route : NavKey {
    /**
     * Analytics identifier logged to Firebase when navigating to this destination.
     */
    val analyticsScreenName: String

    @Serializable
    data object Onboarding : Route, NavKey {
        override val analyticsScreenName: String get() = AnalyticsScreen.ONBOARDING
    }

    @Serializable
    data object Home : Route, NavKey {
        override val analyticsScreenName: String get() = AnalyticsScreen.HOME
    }

    @Serializable
    data object LoginWebView : Route, NavKey {
        override val analyticsScreenName: String get() = AnalyticsScreen.LOGIN_WEBVIEW
    }

    @Serializable
    data object ForceUpdate : Route, NavKey {
        override val analyticsScreenName: String get() = AnalyticsScreen.FORCE_UPDATE
    }

    @Serializable
    data object Downloads : Route, NavKey {
        override val analyticsScreenName: String get() = AnalyticsScreen.DOWNLOADS
    }

    @Serializable
    data object Subscription : Route, NavKey {
        override val analyticsScreenName: String get() = AnalyticsScreen.SUBSCRIPTION
    }

    @Serializable
    data class Story(
        val storyId: String,
        val isHighlight: Boolean = false
    ) : Route, NavKey {
        override val analyticsScreenName: String get() = AnalyticsScreen.STORY
    }

    @Serializable
    data class Highlight(
        val userId: String,
        val userName: String
    ) : Route, NavKey {
        override val analyticsScreenName: String get() = AnalyticsScreen.HIGHLIGHT
    }

    companion object {
        /**
         * Resolves a deep-link URI into a typed [Route].
         *
         * Supported schemes:
         * - `storyghost://subscription` -> [Route.Subscription]
         * - `storyghost://downloads` -> [Route.Downloads]
         * - `storyghost://home` -> [Route.Home]
         * - `storyghost://story?id={id}&isHighlight={boolean}` -> [Route.Story]
         * - `storyghost://highlight?userId={id}&userName={name}` -> [Route.Highlight]
         */
        fun fromDeepLink(urlString: String?): Route? {
            if (urlString.isNullOrBlank()) return null
            val uri = try {
                urlString.toUri()
            } catch (_: Exception) {
                return null
            }

            if (uri.scheme?.lowercase() != "storyghost") return null

            val host = uri.host?.lowercase() ?: uri.path?.trimStart('/')?.lowercase()

            return when (host) {
                "subscription" -> Subscription
                "downloads" -> Downloads
                "home" -> Home
                "story" -> {
                    val id = uri.getQueryParameter("id") ?: uri.getQueryParameter("storyId")
                    if (!id.isNullOrBlank()) {
                        val isHighlight = uri.getBooleanQueryParameter("isHighlight", false)
                        Story(storyId = id, isHighlight = isHighlight)
                    } else null
                }
                "highlight" -> {
                    val userId = uri.getQueryParameter("userId") ?: uri.getQueryParameter("id")
                    val userName = uri.getQueryParameter("userName") ?: uri.getQueryParameter("username") ?: ""
                    if (!userId.isNullOrBlank()) {
                        Highlight(userId = userId, userName = userName)
                    } else null
                }
                else -> null
            }
        }
    }
}
