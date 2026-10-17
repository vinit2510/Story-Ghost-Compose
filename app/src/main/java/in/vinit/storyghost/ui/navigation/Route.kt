package `in`.vinit.storyghost.ui.navigation

import androidx.navigation3.runtime.NavKey
import `in`.vinit.storyghost.core.analytics.AnalyticsScreen
import kotlinx.serialization.Serializable

/**
 * Sealed interface representing type-safe navigation destinations for the app.
 *
 * Implements [NavKey] for compatibility with AndroidX Navigation 3 and provides
 * an [analyticsScreenName] for automatic screen view event tracking.
 */
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
}
