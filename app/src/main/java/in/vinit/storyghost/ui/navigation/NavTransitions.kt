package `in`.vinit.storyghost.ui.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.graphics.TransformOrigin
import androidx.navigation3.runtime.metadata
import androidx.navigation3.ui.NavDisplay

private const val TRANSITION_DURATION_ENTER = 350
private const val TRANSITION_DURATION_EXIT = 300
private const val SCALE_BACKGROUND = 0.94f

/**
 * Forward push horizontal transition:
 * - Entering screen slides in from right while fading in.
 * - Exiting background screen scales down without fading.
 */
fun horizontalTransition(): ContentTransform =
    (slideInHorizontally(
        animationSpec = tween(TRANSITION_DURATION_ENTER, easing = FastOutSlowInEasing),
        initialOffsetX = { fullWidth -> fullWidth }
    ) + fadeIn(
        animationSpec = tween(TRANSITION_DURATION_ENTER, easing = FastOutSlowInEasing)
    )) togetherWith scaleOut(
        animationSpec = tween(TRANSITION_DURATION_ENTER, easing = FastOutSlowInEasing),
        targetScale = SCALE_BACKGROUND,
        transformOrigin = TransformOrigin.Center
    )

/**
 * Pop horizontal transition (used for back button and predictive back):
 * - Entering background screen scales up and fades in.
 * - Exiting screen slides out to the right without scaling or fading.
 */
fun horizontalPopTransition(): ContentTransform =
    (scaleIn(
        animationSpec = tween(TRANSITION_DURATION_EXIT, easing = FastOutSlowInEasing),
        initialScale = SCALE_BACKGROUND,
        transformOrigin = TransformOrigin.Center
    ) + fadeIn(
        animationSpec = tween(TRANSITION_DURATION_EXIT, easing = FastOutSlowInEasing)
    )) togetherWith slideOutHorizontally(
        animationSpec = tween(TRANSITION_DURATION_EXIT, easing = FastOutSlowInEasing),
        targetOffsetX = { fullWidth -> fullWidth }
    )

/**
 * Forward vertical transition (bottom sheet / modal style):
 * - Entering screen slides in from bottom while fading in.
 * - Exiting background screen scales down without fading.
 */
fun verticalTransition(): ContentTransform =
    (slideInVertically(
        animationSpec = tween(TRANSITION_DURATION_ENTER, easing = FastOutSlowInEasing),
        initialOffsetY = { fullHeight -> fullHeight }
    ) + fadeIn(
        animationSpec = tween(TRANSITION_DURATION_ENTER, easing = FastOutSlowInEasing)
    )) togetherWith scaleOut(
        animationSpec = tween(TRANSITION_DURATION_ENTER, easing = FastOutSlowInEasing),
        targetScale = SCALE_BACKGROUND,
        transformOrigin = TransformOrigin.Center
    )

/**
 * Pop vertical transition (bottom sheet / modal style):
 * - Entering background screen scales up and fades in.
 * - Exiting screen slides down without scaling or fading.
 */
fun verticalPopTransition(): ContentTransform =
    (scaleIn(
        animationSpec = tween(TRANSITION_DURATION_EXIT, easing = FastOutSlowInEasing),
        initialScale = SCALE_BACKGROUND,
        transformOrigin = TransformOrigin.Center
    ) + fadeIn(
        animationSpec = tween(TRANSITION_DURATION_EXIT, easing = FastOutSlowInEasing)
    )) togetherWith slideOutVertically(
        animationSpec = tween(TRANSITION_DURATION_EXIT, easing = FastOutSlowInEasing),
        targetOffsetY = { fullHeight -> fullHeight }
    )

/**
 * Modal bottom sheet-style transition metadata applied to routes that slide up:
 * - [Route.Story], [Route.Highlight], [Route.Subscription], and [Route.LoginWebView].
 */
val slideUpTransitionMetadata = metadata {
    put(NavDisplay.TransitionKey) {
        verticalTransition()
    }
    put(NavDisplay.PopTransitionKey) {
        verticalPopTransition()
    }
    put(NavDisplay.PredictivePopTransitionKey) { _ ->
        verticalPopTransition()
    }
}
