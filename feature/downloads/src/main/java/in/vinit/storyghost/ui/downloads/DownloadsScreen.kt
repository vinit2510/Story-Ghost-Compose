package `in`.vinit.storyghost.ui.downloads

import android.widget.Toast
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import `in`.vinit.storyghost.core.common.FileUtils
import `in`.vinit.storyghost.core.ui.R
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.component.ErrorPlaceholder
import `in`.vinit.storyghost.ui.theme.StoryGhostTheme
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.util.Date
import java.util.Locale

@Composable
fun DownloadsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DownloadsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.fileList.collectAsStateWithLifecycle()

    DownloadsContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onShareClick = { file -> FileUtils.shareFile(context, file) },
        onDeleteClick = { file ->
            viewModel.deleteDownload(file) {
                Toast.makeText(context, R.string.toast_file_deleted, Toast.LENGTH_SHORT).show()
            }
        },
        onItemClick = { file -> FileUtils.openFile(context, file) },
        onRetryClick = { viewModel.loadDownloads() },
        modifier = modifier
    )
}

@Composable
fun DownloadsContent(
    uiState: UiState<List<File>>,
    onBackClick: () -> Unit,
    onShareClick: (File) -> Unit,
    onDeleteClick: (File) -> Unit,
    onItemClick: (File) -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 15.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_downloads),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = stringResource(R.string.downloads),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "Close",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            if (uiState is UiState.Loading) {
                DownloadsShimmerLoader()
            } else if (uiState is UiState.Error) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorPlaceholder(
                        message = uiState.errorMsg.asString(),
                        modifier = Modifier.fillMaxWidth(),
                        onRetryClick = onRetryClick
                    )
                }
            } else if (uiState is UiState.Success) {
                val fileList = uiState.data
                if (fileList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorPlaceholder(
                            message = stringResource(R.string.no_downloads_found),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(fileList, key = { it.absolutePath }) { file ->
                            DownloadItemRow(
                                file = file,
                                onShareClick = { onShareClick(file) },
                                onDeleteClick = { onDeleteClick(file) },
                                onItemClick = { onItemClick(file) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadItemRow(
    file: File,
    onShareClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageRequest = remember(file, context) {
        ImageRequest.Builder(context)
            .data(file)
            .crossfade(true)
            .build()
    }
    val isVideo = remember(file) { file.extension.equals("mp4", ignoreCase = true) }
    val formattedDate = remember(file, context) { formatFileTimestamp(context, file.lastModified()) }
    val formattedSize = remember(file) { FileUtils.getReadableFileSize(file) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            AsyncImage(
                model = imageRequest,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            if (isVideo) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_play),
                        contentDescription = "Play",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Details
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = file.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(3.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Icon(
                    painter = painterResource(R.drawable.ic_dot),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.padding(horizontal = 5.dp)
                )
                Text(
                    text = formattedSize,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        IconButton(onClick = onShareClick) {
            Icon(
                painter = painterResource(R.drawable.ic_share),
                contentDescription = "Share",
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }

        IconButton(onClick = onDeleteClick) {
            Icon(
                painter = painterResource(R.drawable.ic_delete),
                contentDescription = "Delete",
                tint = Color.Unspecified,
            )
        }

        Spacer(modifier = Modifier.width(7.dp))
    }
}

@Composable
fun DownloadsShimmerLoader(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha = infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )
    val shimmerColor = MaterialTheme.colorScheme.surface

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { this.alpha = alpha.value },
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(30) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(shimmerColor)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 140.dp, height = 16.dp)
                            .background(shimmerColor)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .size(width = 90.dp, height = 12.dp)
                            .background(shimmerColor)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DownloadsContentPreview() {
    val mockFiles = listOf(
        File("story_video.mp4"),
        File("story_image.jpg")
    )
    StoryGhostTheme {
        DownloadsContent(
            uiState = UiState.Success(mockFiles),
            onBackClick = {},
            onShareClick = {},
            onDeleteClick = {},
            onItemClick = {},
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DownloadsShimmerPreview() {
    StoryGhostTheme {
        DownloadsContent(
            uiState = UiState.Loading,
            onBackClick = {},
            onShareClick = {},
            onDeleteClick = {},
            onItemClick = {},
            onRetryClick = {}
        )
    }
}

private fun formatFileTimestamp(context: android.content.Context, timeInMillis: Long): String {
    if (timeInMillis <= 0) return ""
    return try {
        val eventTime = Instant.ofEpochMilli(timeInMillis)
        val currentTime = Instant.now()
        val duration = Duration.between(eventTime, currentTime)
        val minutesAgo = duration.toMinutes()
        val hoursAgo = duration.toHours()

        when {
            minutesAgo < 60 -> context.resources.getQuantityString(
                R.plurals.time_minutes_ago,
                minutesAgo.toInt(),
                minutesAgo
            )

            hoursAgo < 24 -> context.resources.getQuantityString(
                R.plurals.time_hours_ago,
                hoursAgo.toInt(),
                hoursAgo
            )

            else -> {
                val dateFormat = SimpleDateFormat("dd MMM yy", Locale.getDefault())
                dateFormat.format(Date(timeInMillis))
            }
        }
    } catch (_: Exception) {
        ""
    }
}
