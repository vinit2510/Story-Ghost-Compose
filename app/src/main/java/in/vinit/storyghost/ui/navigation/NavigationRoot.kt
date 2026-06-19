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
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.forceupdate.ForceUpdateScreen
import `in`.vinit.storyghost.ui.home.HomeScreen
import `in`.vinit.storyghost.ui.onboarding.LoginWebViewScreen
import `in`.vinit.storyghost.ui.onboarding.OnboardingScreen
import `in`.vinit.storyghost.ui.story.HighlightScreen
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
            backStack.clear()
            backStack.add(route)
        }
    }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
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
                                backStack.remove(key)
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
                                backStack.remove(key)
                                backStack.remove(Route.Onboarding)
                                backStack.add(Route.Home)
                            }
                        )
                    }
                }

                is Route.Home -> {
                    NavEntry(key) {
                        HomeScreen(
                            onStoryClick = { userId, userName, isHighlight ->
                                if (isHighlight) {
                                    backStack.add(Route.Highlight(userId, userName))
                                } else {
                                    backStack.add(
                                        Route.Story(
                                            storyId = userId,
                                            isHighlight = false
                                        )
                                    )
                                }
                            },
                            onLoginClick = {
                                backStack.add(Route.LoginWebView)
                            }
                        )
                    }
                }

                is Route.Story -> {
                    NavEntry(key) {
                        StoryScreen(
                            storyId = key.storyId,
                            isHighlight = key.isHighlight,
                            onBackClick = { backStack.remove(key) },
                            onHighlightClick = { userId, userName ->
                                backStack.add(Route.Highlight(userId, userName))
                            }
                        )
                    }
                }

                is Route.Highlight -> {
                    NavEntry(key) {
                        HighlightScreen(
                            userId = key.userId,
                            userName = key.userName,
                            onBackClick = { backStack.remove(key) },
                            onHighlightClick = { highlightId ->
                                backStack.add(
                                    Route.Story(
                                        storyId = highlightId,
                                        isHighlight = true
                                    )
                                )
                            }
                        )
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