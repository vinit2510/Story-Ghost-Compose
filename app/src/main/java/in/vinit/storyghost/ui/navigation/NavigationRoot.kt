package `in`.vinit.storyghost.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import `in`.vinit.storyghost.ui.home.HomeScreen
import `in`.vinit.storyghost.ui.onboarding.OnboardingScreen
import `in`.vinit.storyghost.ui.story.StoryScreen

@Composable
fun NavigationRoot(modifier: Modifier = Modifier) {

    val backStack = rememberNavBackStack(Route.Onboarding)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryProvider = { key ->
            when (key) {
                is Route.Onboarding -> {
                    NavEntry(key) {
                        OnboardingScreen(onboardingComplete = {
                            backStack.remove(Route.Onboarding)
                            backStack.add(Route.Home)
                        })
                    }
                }

                is Route.Home -> {
                    NavEntry(key) {
                        HomeScreen(onStoryClick = { storyId ->
                            backStack.add(Route.Story(storyId))
                        })
                    }
                }

                is Route.Story -> {
                    NavEntry(key) {
                        StoryScreen(key.storyId)
                    }
                }

                else -> error("Invalid nav key: $key")
            }

        }
    )
}