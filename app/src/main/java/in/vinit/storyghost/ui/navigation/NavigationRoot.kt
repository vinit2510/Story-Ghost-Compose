package `in`.vinit.storyghost.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.metadata
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import `in`.vinit.storyghost.core.analytics.LocalAnalyticsLogger
import `in`.vinit.storyghost.ui.downloads.DownloadsScreen
import `in`.vinit.storyghost.ui.forceupdate.ForceUpdateScreen
import `in`.vinit.storyghost.ui.highlight.HighlightScreen
import `in`.vinit.storyghost.ui.home.HomeScreen
import `in`.vinit.storyghost.ui.login.LoginDialog
import `in`.vinit.storyghost.ui.login.LoginWebViewScreen
import `in`.vinit.storyghost.ui.login.LoginWithCookieDialog
import `in`.vinit.storyghost.ui.login.LogoutDialog
import `in`.vinit.storyghost.ui.onboarding.OnboardingScreen
import `in`.vinit.storyghost.ui.story.StoryScreen
import `in`.vinit.storyghost.ui.subscription.SubscriptionScreen

/**
 * Root navigation host managing the type-safe backstack using AndroidX Navigation 3.
 *
 * Handles:
 * - Backstack state preservation with [rememberSaveableStateHolderNavEntryDecorator] and [rememberViewModelStoreNavEntryDecorator].
 * - Screen tracking and analytics logging via [LocalAnalyticsLogger].
 * - Animated horizontal and vertical slide transitions between destinations.
 * - Routing between all feature screens: Home, Story, Highlight, Downloads, Subscription, Login, and Onboarding.
 *
 * @param initialRoute The initial destination calculated at startup (e.g., Home, Onboarding, or ForceUpdate).
 * @param modifier The layout modifier for the navigation display container.
 */
@Composable
fun NavigationRoot(
    initialRoute: Route,
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(initialRoute)
    val popBackStack: () -> Unit = { backStack.removeLastOrNull() }
    val analyticsLogger = LocalAnalyticsLogger.current

    val currentRoute = backStack.lastOrNull()

    LaunchedEffect(currentRoute) {
        val route = currentRoute as? Route ?: return@LaunchedEffect
        analyticsLogger.logScreenView(route.analyticsScreenName)
    }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = popBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        transitionSpec = {
            slideInHorizontally(initialOffsetX = { it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { -it })
        },
        popTransitionSpec = {
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
        },
        predictivePopTransitionSpec = { _ ->
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
        },
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
                            },
                            renderCookieLoginDialog = { onDismiss, onLoginSuccess ->
                                LoginWithCookieDialog(
                                    onDismissRequest = onDismiss,
                                    onLoginSuccess = onLoginSuccess
                                )
                            }
                        )
                    }
                }

                is Route.LoginWebView -> {
                    NavEntry(key, metadata = slideUpTransitionMetadata) {
                        LoginWebViewScreen(
                            onLoginSuccess = {
                                backStack.clear()
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
                            onPremiumClick = {
                                backStack.add(Route.Subscription)
                            },
                            onLoginClick = {
                                backStack.add(Route.LoginWebView)
                            },
                            renderLoginDialog = { onDismiss ->
                                LoginDialog(
                                    onLoginClick = {
                                        onDismiss()
                                        backStack.add(Route.LoginWebView)
                                    },
                                    onCookieLoginSuccess = { onDismiss() },
                                    onDismissRequest = onDismiss
                                )
                            },
                            renderLogoutDialog = { onDismiss, onLogout ->
                                LogoutDialog(
                                    onLogoutSuccess = {
                                        onDismiss()
                                        onLogout()
                                    },
                                    onDismissRequest = onDismiss
                                )
                            }
                        )
                    }
                }

                is Route.Story -> {
                    NavEntry(key, metadata = slideUpTransitionMetadata) {
                        StoryScreen(
                            storyId = key.storyId,
                            isHighlight = key.isHighlight,
                            onHighlightClick = { userId, userName ->
                                backStack.add(Route.Highlight(userId, userName))
                            }
                        )
                    }
                }

                is Route.Highlight -> {
                    NavEntry(key, metadata = slideUpTransitionMetadata) {
                        HighlightScreen(
                            userId = key.userId,
                            userName = key.userName,
                            onBackClick = popBackStack,
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
                            onBackClick = popBackStack
                        )
                    }
                }

                is Route.Subscription -> {
                    NavEntry(key, metadata = slideUpTransitionMetadata) {
                        SubscriptionScreen(
                            onBackClick = popBackStack
                        )
                    }
                }

                else -> error("Invalid nav key: $key")
            }

        }
    )
}

private val slideUpTransitionMetadata = metadata {
    put(NavDisplay.TransitionKey) {
        slideInVertically(initialOffsetY = { it }) togetherWith
                ExitTransition.KeepUntilTransitionsFinished
    }
    put(NavDisplay.PopTransitionKey) {
        EnterTransition.None togetherWith
                slideOutVertically(targetOffsetY = { it })
    }
    put(NavDisplay.PredictivePopTransitionKey) { _ ->
        EnterTransition.None togetherWith
                slideOutVertically(targetOffsetY = { it })
    }
}