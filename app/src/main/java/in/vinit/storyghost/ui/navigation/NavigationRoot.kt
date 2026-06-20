package `in`.vinit.storyghost.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import `in`.vinit.storyghost.ui.downloads.DownloadsScreen
import `in`.vinit.storyghost.ui.forceupdate.ForceUpdateScreen
import `in`.vinit.storyghost.ui.home.HomeScreen
import `in`.vinit.storyghost.ui.onboarding.LoginWebViewScreen
import `in`.vinit.storyghost.ui.onboarding.OnboardingScreen
import `in`.vinit.storyghost.ui.story.HighlightScreen
import `in`.vinit.storyghost.ui.story.StoryScreen

@Composable
fun NavigationRoot(
    initialRoute: Route,
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(initialRoute)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = { key ->
            when (key) {
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
                            onDownloadsClick = {
                                backStack.add(Route.Downloads)
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

                is Route.Downloads -> {
                    NavEntry(key) {
                        DownloadsScreen(
                            onBackClick = { backStack.remove(key) }
                        )
                    }
                }

                else -> error("Invalid nav key: $key")
            }

        }
    )
}