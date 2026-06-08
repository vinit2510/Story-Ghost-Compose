package `in`.vinit.storyghost.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.forceupdate.ForceUpdateScreen
import `in`.vinit.storyghost.ui.home.HomeScreen
import `in`.vinit.storyghost.ui.onboarding.LoginWebViewScreen
import `in`.vinit.storyghost.ui.onboarding.OnboardingScreen
import `in`.vinit.storyghost.ui.story.StoryScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
    viewModel: NavViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val backStack = rememberNavBackStack(Route.Splash)

    LaunchedEffect(uiState is UiState.Success) {
        if (uiState is UiState.Success) {
            val route = (uiState as UiState.Success).data
            backStack.add(route)
            backStack.remove(Route.Splash)
        }
    }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryProvider = { key ->
            when (key) {
                is Route.Splash -> {
                    NavEntry(key) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                        )
                    }
                }

                is Route.Onboarding -> {
                    NavEntry(key) {
                        OnboardingScreen(
                            onboardingComplete = {
                                backStack.remove(Route.Onboarding)
                                backStack.add(Route.Home)
                            },
                            onLoginClick = {
                                backStack.add(Route.LoginWebView)
                            }
                        )
                    }
                }

                is Route.LoginWebView -> {
                    NavEntry(key) {
                        LoginWebViewScreen(
                            onLoginSuccess = {
                                backStack.remove(Route.LoginWebView)
                                backStack.remove(Route.Onboarding)
                                backStack.add(Route.Home)
                            }
                        )
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

                is Route.ForceUpdate -> {
                    NavEntry(key) {
                        ForceUpdateScreen()
                    }
                }

                else -> error("Invalid nav key: $key")
            }

        }
    )
}