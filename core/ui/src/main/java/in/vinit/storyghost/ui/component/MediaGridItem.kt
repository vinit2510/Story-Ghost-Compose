package `in`.vinit.storyghost.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import `in`.vinit.storyghost.core.ui.R
import `in`.vinit.storyghost.ui.theme.StoryGhostTheme

@Composable
fun MediaGridItem(
    title: String,
    coverUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    prefixUsername: Boolean = false,
    isVerified: Boolean = false
) {
    val context = LocalContext.current
    val imageRequest = remember(coverUrl, context) {
        ImageRequest.Builder(context)
            .data(coverUrl)
            .crossfade(true)
            .build()
    }

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.75f),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = imageRequest,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(10.dp),
                error = painterResource(R.drawable.avatar_instagram_user)
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = imageRequest,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .border(
                            3.dp,
                            MaterialTheme.colorScheme.outline,
                            CircleShape
                        ),
                    error = painterResource(R.drawable.avatar_instagram_user)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    shape = MaterialTheme.shapes.extraSmall,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    )
                ) {
                    val displayText = if (prefixUsername) {
                        "@$title"
                    } else {
                        title
                    }
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = displayText,
                            modifier = Modifier.weight(1f, fill = false),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2
                        )
                        if (isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Image(
                                painter = painterResource(R.drawable.ic_verified),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MediaGridItemPreview() {
    StoryGhostTheme {
        MediaGridItem(
            title = "Vacation",
            coverUrl = "",
            onClick = {},
            isVerified = true
        )
    }
}
