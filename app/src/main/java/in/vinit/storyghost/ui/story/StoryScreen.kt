package `in`.vinit.storyghost.ui.story

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import `in`.vinit.storyghost.R
import `in`.vinit.storyghost.data.model.UserStoryResponse.Data.ReelsMedia.Item
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.component.ErrorPlaceholder
import `in`.vinit.storyghost.ui.theme.StoryGhostTheme
import `in`.vinit.storyghost.util.TimeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun StoryScreen(
    storyId: String,
    isHighlight: Boolean,
    onBackClick: () -> Unit,
    onHighlightClick: (userId: String, userName: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StoryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler {
        onBackClick()
    }

    LaunchedEffect(storyId) {
        viewModel.loadStories(storyId, isHighlight)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is UiState.Loading -> {
                    StoryShimmerItem()
                }

                is UiState.Error -> {
                    ErrorPlaceholder(
                        message = state.errorMsg.asString()
                    )
                }

                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        ErrorPlaceholder(
                            message = if (isHighlight)
                                "Highlight not available at this moment"
                            else
                                "Story not available at this moment"
                        )
                    } else {
                        StoryContent(
                            stories = state.data,
                            isHighlight = isHighlight,
                            initialPage = viewModel.currentPageIndex,
                            onPageChanged = { viewModel.currentPageIndex = it },
                            onHighlightClick = onHighlightClick,
                            onDownloadClick = viewModel::downloadStory
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StoryContent(
    stories: List<Item>,
    isHighlight: Boolean,
    initialPage: Int,
    onPageChanged: (Int) -> Unit,
    onHighlightClick: (userId: String, userName: String) -> Unit,
    onDownloadClick: (Item, () -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { stories.size }
    )

    LaunchedEffect(pagerState.currentPage) {
        onPageChanged(pagerState.currentPage)
    }
    var isWatchingSecretlyVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500)
        isWatchingSecretlyVisible = true
        delay(3000)
        isWatchingSecretlyVisible = false
    }

    Box(modifier = modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                StoryPage(
                    data = stories[page],
                    currentCount = page,
                    totalCount = stories.size,
                    isHighlight = isHighlight,
                    isActive = pagerState.currentPage == page,
                    onHighlightClick = onHighlightClick,
                    onDownloadClick = onDownloadClick
                )
            }
        }

        // Watching Secretly Top Banner Overlay
        AnimatedVisibility(
            visible = isWatchingSecretlyVisible,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp)
        ) {
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_hide),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Watching Secretly",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun StoryPage(
    data: Item,
    currentCount: Int,
    totalCount: Int,
    isHighlight: Boolean,
    isActive: Boolean,
    onHighlightClick: (userId: String, userName: String) -> Unit,
    onDownloadClick: (Item, () -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isDownloaded by remember(data) { mutableStateOf(false) }
    var lastShownIndex by remember(data) { mutableIntStateOf(-1) }
    val selectedMentionIndex = remember(data) { mutableIntStateOf(-1) }
    var isShimmeringMentions by remember(data) { mutableStateOf(false) }

    val mentionList = remember(data) {
        data.tappableObjects?.filter { it.typename == "GraphTappableMention" } ?: emptyList()
    }

    val timestampText = remember(data.takenAtTimeStamp) {
        data.takenAtTimeStamp?.let { TimeUtils.getStoryTimestamp(it) } ?: ""
    }

    val countText = "${currentCount + 1}/${totalCount}"

    val mentionText = when {
        isShimmeringMentions -> stringResource(R.string.hidden_mention)
        lastShownIndex == -2 -> stringResource(R.string.zero_mention)
        lastShownIndex == -1 -> stringResource(R.string.hidden_mention)
        else -> stringResource(
            R.string.mention_count,
            lastShownIndex + 1,
            mentionList.size
        )
    }

    val activeMention =
        if (selectedMentionIndex.intValue >= 0 && selectedMentionIndex.intValue < mentionList.size) {
            mentionList[selectedMentionIndex.intValue]
        } else {
            null
        }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 10.dp)
    ) {
        // Media Card Frame
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
        ) {
            Card(
                shape = MaterialTheme.shapes.small,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxSize(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val containerWidth = maxWidth
                    val containerHeight = maxHeight

                    // 1. Image / Video Player rendering
                    if (data.typename == "GraphStoryVideo") {
                        val videoUrl = data.videoResources?.getOrNull(0)?.src ?: ""
                        VideoPlayer(
                            videoUrl = videoUrl,
                            isActive = isActive,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        val imageUrl = data.displayUrl ?: ""
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            error = painterResource(R.drawable.avatar_instagram_user)
                        )
                    }

                    // 2. Profile Section overlay (top-left)
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(data.owner?.profilePicUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                            placeholder = painterResource(R.drawable.avatar_instagram_user),
                            error = painterResource(R.drawable.avatar_instagram_user)
                        )
                        Text(
                            text = data.owner?.username ?: "",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    // 4. Mentions Search Shimmer/Progress overlay
                    if (isShimmeringMentions) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f))
                        ) {
                            ShimmerOverlay(modifier = Modifier.fillMaxSize())
                        }
                    }

                    // 5. Active Mention overlay positioned relatively
                    activeMention?.let { mention ->
                        val density = LocalDensity.current
                        val containerWidthPx = with(density) { containerWidth.toPx() }
                        val containerHeightPx = with(density) { containerHeight.toPx() }

                        val xPos = mention.x.toFloat() * containerWidthPx
                        val yPos = mention.y.toFloat() * containerHeightPx

                        MentionPopup(
                            username = mention.username ?: "",
                            fullName = mention.fullName ?: "",
                            xOffset = xPos,
                            yOffset = yPos,
                            onDismiss = {
                                selectedMentionIndex.intValue = -1
                            },
                            onClick = {
                                openInstagramAccount(context, mention.username ?: "")
                            }
                        )
                    }
                }
            }
        }

        // Action controls and metadata row at bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Meta column (left)
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = timestampText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = countText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            // Buttons row (right)
            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Download button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.clickable {
                        if (!isDownloaded) {
                            onDownloadClick(data) {
                                isDownloaded = true
                            }
                        }
                    }
                ) {
                    Card(
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.size(40.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (isDownloaded) R.drawable.ic_tick else R.drawable.ic_downloads
                                ),
                                contentDescription = "Download",
                                tint = if (isDownloaded) MaterialTheme.colorScheme.tertiary else Color.Unspecified,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Text(
                        text = if (isDownloaded)
                            stringResource(R.string.saved_successfully)
                        else
                            stringResource(R.string.download_story),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                }

                // Highlights button (visible only in feed/active story mode)
                if (!isHighlight) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.clickable {
                            data.owner?.let { owner ->
                                onHighlightClick(owner.id ?: "", owner.username ?: "")
                            }
                        }
                    ) {
                        Card(
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.size(40.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_highlight),
                                    contentDescription = "Highlights",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Text(
                            text = stringResource(R.string.show_highlights),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Mentions button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.clickable {
                        if (isShimmeringMentions) return@clickable
                        if (mentionList.isEmpty()) {
                            isShimmeringMentions = true
                            coroutineScope.launch {
                                delay(1000)
                                isShimmeringMentions = false
                                Toast.makeText(
                                    context,
                                    "This story does not have any mention",
                                    Toast.LENGTH_SHORT
                                ).show()
                                selectedMentionIndex.intValue = -2
                                lastShownIndex = -2
                            }
                        } else {
                            val nextIndex = lastShownIndex + 1
                            if (nextIndex >= mentionList.size) {
                                selectedMentionIndex.intValue = -1
                                lastShownIndex = -1
                            } else {
                                lastShownIndex = nextIndex
                                isShimmeringMentions = true
                                coroutineScope.launch {
                                    delay(1000)
                                    isShimmeringMentions = false
                                    selectedMentionIndex.intValue = nextIndex
                                }
                            }
                        }
                    }
                ) {
                    Card(
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.size(40.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_mention_users),
                                contentDescription = "Mentions",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Text(
                        text = mentionText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private fun openInstagramAccount(context: Context, username: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    val url = "https://www.instagram.com/$username/"
    intent.data = url.toUri()
    try {
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "Instagram app not found", Toast.LENGTH_SHORT).show()
    }
}

@Preview(showBackground = true)
@Composable
fun StoryPagePreview() {
    val mockItem = Item(
        typename = "GraphStoryImage",
        id = "12345",
        takenAtTimeStamp = 1680000000L,
        owner = Item.Owner(
            id = "owner_id",
            profilePicUrl = "https://example.com/pic.jpg",
            username = "johndoe"
        ),
        displayUrl = "https://example.com/story.jpg",
        tappableObjects = arrayListOf(
            Item.TappableObject(
                typename = "GraphTappableMention",
                x = 0.5,
                y = 0.5,
                username = "janedoe",
                fullName = "Jane Doe"
            )
        )
    )

    StoryGhostTheme {
        StoryPage(
            data = mockItem,
            currentCount = 0,
            totalCount = 1,
            isHighlight = false,
            isActive = true,
            onHighlightClick = { _, _ -> },
            onDownloadClick = { _, _ -> }
        )
    }
}