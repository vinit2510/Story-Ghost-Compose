package `in`.vinit.storyghost.ui.story

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import `in`.vinit.storyghost.core.ui.R
import `in`.vinit.storyghost.ui.theme.StoryGhostTheme

import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun ShimmerOverlay(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer_overlay")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerColor = MaterialTheme.colorScheme.onSurface
    val shimmerColors = remember(shimmerColor) {
        listOf(
            shimmerColor.copy(alpha = 0.0f),
            shimmerColor.copy(alpha = 0.3f),
            shimmerColor.copy(alpha = 0.0f)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                val offset = translateAnim.value
                val brush = Brush.linearGradient(
                    colors = shimmerColors,
                    start = Offset(x = offset - 500f, y = offset - 500f),
                    end = Offset(x = offset, y = offset)
                )
                drawRect(brush = brush)
            }
    )
}

@Composable
fun StoryShimmerItem(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha = infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )
    val shimmerColor = MaterialTheme.colorScheme.surface

    Column(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { this.alpha = alpha.value }
            .padding(vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                .clip(MaterialTheme.shapes.small)
                .background(shimmerColor)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Yesterday, 10:00 PM",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Transparent,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(shimmerColor)
                )
                Text(
                    text = "1/10",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Transparent,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(shimmerColor)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(shimmerColor)
                    )
                    Text(
                        text = stringResource(R.string.download_story),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Transparent,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(shimmerColor)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(shimmerColor)
                    )
                    Text(
                        text = stringResource(R.string.hidden_mention),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Transparent,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(shimmerColor)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StoryShimmerItemPreview() {
    StoryGhostTheme {
        StoryShimmerItem()
    }
}
