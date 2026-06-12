package `in`.vinit.storyghost.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
sealed interface Route : NavKey {

    @Serializable
    data object Splash : Route, NavKey

    @Serializable
    data object Onboarding : Route, NavKey

    @Serializable
    data object Home : Route, NavKey

    @Serializable
    data class LoginWebView(val id: String = UUID.randomUUID().toString()) : Route, NavKey



    @Serializable
    data object ForceUpdate : Route, NavKey

    @Serializable
    data class Story(val storyId: String) : Route, NavKey
}

