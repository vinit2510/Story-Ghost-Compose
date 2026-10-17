package `in`.vinit.storyghost.ui.component

import androidx.annotation.OptIn
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.media3.ui.compose.state.rememberPresentationState

/**
 * Hardware-accelerated video player Composable backed by AndroidX Media3 [ExoPlayer].
 *
 * Features:
 * - Plays H.264/MP4 video streams with looping playback enabled.
 * - Manages ExoPlayer lifecycle bound to the Android lifecycle (pauses on backgrounding, resumes on return).
 * - Displays a shimmering placeholder animation while the video is buffering.
 * - Uses [SURFACE_TYPE_TEXTURE_VIEW] and [resizeWithContentScale] for smooth Compose rendering.
 *
 * @param videoUrl Remote or local URI string of the video media.
 * @param isActive Flag controlling playback; player only plays when true, pausing when scrolled off-screen.
 * @param modifier The layout modifier for the player container.
 */
@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    videoUrl: String,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
        }
    }

    var isBuffering by remember { mutableStateOf(true) }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering =
                    playbackState == Player.STATE_BUFFERING || playbackState == Player.STATE_IDLE
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    LaunchedEffect(videoUrl) {
        isBuffering = true
        exoPlayer.setMediaItem(MediaItem.fromUri(videoUrl.toUri()))
        exoPlayer.prepare()
    }

    var isResumed by remember { mutableStateOf(true) }

    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        isResumed = false
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        isResumed = true
    }

    // Single source of truth driving playback states
    val shouldPlay = isActive && isResumed

    LaunchedEffect(shouldPlay) {
        exoPlayer.playWhenReady = shouldPlay
    }

    val presentationState = rememberPresentationState(exoPlayer)

    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
    ) {
        // We use SURFACE_TYPE_TEXTURE_VIEW to allow proper Compose layout clipToBounds() / transformations
        // and to completely avoid SurfaceSyncGroup synchronization timeouts on API 34+ when swiping.
        PlayerSurface(
            player = exoPlayer,
            surfaceType = SURFACE_TYPE_TEXTURE_VIEW,
            modifier = Modifier
                .fillMaxSize()
                .resizeWithContentScale(
                    contentScale = ContentScale.Crop,
                    sourceSizeDp = presentationState.videoSizeDp
                )
        )

        if (isActive && isBuffering) {
            VideoShimmerLoader(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun VideoShimmerLoader(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "video_shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "video_shimmer_translate"
    )

    val shimmerColor = MaterialTheme.colorScheme.onSurface
    val baseColor = shimmerColor.copy(alpha = 0.0f)
    val highlightColor = shimmerColor.copy(alpha = 0.25f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                val sweepHeight = size.height * 0.4f
                val startY = (translateAnim * (size.height + sweepHeight)) - sweepHeight
                val endY = startY + sweepHeight

                val brush = Brush.linearGradient(
                    colors = listOf(baseColor, highlightColor, baseColor),
                    start = Offset(x = 0f, y = startY),
                    end = Offset(x = 0f, y = endY)
                )
                drawRect(brush = brush)
            }
    )
}
