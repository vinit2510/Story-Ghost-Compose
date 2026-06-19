package `in`.vinit.storyghost.ui.story

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import `in`.vinit.storyghost.R
import `in`.vinit.storyghost.ui.theme.StoryGhostTheme

@Composable
fun MentionPopup(
    username: String,
    fullName: String,
    xOffset: Float,
    yOffset: Float,
    onDismiss: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(username) {
        isVisible = true
    }

    Popup(
        alignment = Alignment.TopStart,
        offset = IntOffset(
            x = (xOffset - with(density) { 80.dp.toPx() }).toInt(),
            y = (yOffset - with(density) { 75.dp.toPx() }).toInt()
        ),
        onDismissRequest = {
            onDismiss()
        },
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = scaleIn(
                initialScale = 0f,
                transformOrigin = TransformOrigin(0.5f, 0.5f),
                animationSpec = tween(durationMillis = 500)
            ) + fadeIn(
                animationSpec = tween(durationMillis = 500)
            ),
            exit = scaleOut(
                targetScale = 0f,
                transformOrigin = TransformOrigin(0.5f, 0.5f),
                animationSpec = tween(durationMillis = 500)
            ) + fadeOut(
                animationSpec = tween(durationMillis = 500)
            )
        ) {
            MentionPopupContent(
                username = username,
                fullName = fullName,
                onClick = onClick,
                modifier = modifier
            )
        }
    }
}

@Composable
fun MentionPopupContent(
    username: String,
    fullName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = MaterialTheme.shapes.extraSmall,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.scrim
            ),
            modifier = Modifier.clickable { onClick() }
        ) {
            Box(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(start = 24.dp, end = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.insta_username, username),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = fullName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Image(
                    painter = painterResource(R.drawable.ic_arrow_continue_small),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 2.dp)
                        .size(16.dp)
                )
            }
        }
        Image(
            painter = painterResource(R.drawable.ic_mention_arrow),
            contentDescription = null,
            modifier = Modifier.size(width = 20.dp, height = 15.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MentionPopupPreview() {
    StoryGhostTheme {
        Box(
            modifier = Modifier.padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            MentionPopupContent(
                username = "janedoe",
                fullName = "Jane Doe",
                onClick = {}
            )
        }
    }
}
