package `in`.vinit.storyghost.ui.story

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

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
            playWhenReady = isActive
        }
    }

    val isBuffering = remember { mutableStateOf(true) }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering.value =
                    playbackState == Player.STATE_BUFFERING || playbackState == Player.STATE_IDLE
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    LaunchedEffect(videoUrl) {
        isBuffering.value = true
        exoPlayer.setMediaItem(MediaItem.fromUri(videoUrl.toUri()))
        exoPlayer.prepare()
    }

    LaunchedEffect(isActive) {
        exoPlayer.playWhenReady = isActive
        if (isActive) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }

    val playerViewRef = remember { mutableStateOf<PlayerView?>(null) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    playerViewRef.value?.player = null
                    exoPlayer.pause()
                }

                Lifecycle.Event.ON_RESUME -> {
                    if (isActive) {
                        playerViewRef.value?.player = exoPlayer
                        exoPlayer.play()
                    }
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.release()
        }
    }

    Box(modifier = modifier
        .fillMaxSize()
        .clipToBounds()) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    layoutParams = android.view.ViewGroup.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    player = exoPlayer
                    keepScreenOn = true
                    playerViewRef.value = this
                }
            },
            update = { playerView ->
                playerView.player = exoPlayer
                playerViewRef.value = playerView
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isActive && isBuffering.value) {
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

    // Theme-friendly base and highlight colors (adapts to light and dark modes)
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
