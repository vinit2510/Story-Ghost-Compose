package `in`.vinit.storyghost.ui.story

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.vinit.storyghost.R
import `in`.vinit.storyghost.data.model.HighlightsResponse.Data.User.EdgeHighlightReels.Edge
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.component.ErrorPlaceholder
import `in`.vinit.storyghost.ui.component.MediaGridItem
import `in`.vinit.storyghost.ui.home.storyGridShimmerItems
import `in`.vinit.storyghost.ui.theme.StoryGhostTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun HighlightScreen(
    userId: String,
    userName: String,
    onBackClick: () -> Unit,
    onHighlightClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HighlightViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.loadHighlights(userId)
    }

    HighlightContent(
        userName = userName,
        uiState = uiState,
        onBackClick = onBackClick,
        onHighlightClick = onHighlightClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HighlightContent(
    userName: String,
    uiState: UiState<List<Edge>>,
    onBackClick: () -> Unit,
    onHighlightClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.highlights, userName),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            contentPadding = PaddingValues(
                start = 15.dp,
                end = 15.dp,
                bottom = innerPadding.calculateBottomPadding() + 10.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (uiState) {
                is UiState.Loading -> {
                    storyGridShimmerItems()
                }

                is UiState.Error -> {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        ErrorPlaceholder(
                            message = uiState.errorMsg.asString()
                        )
                    }
                }

                is UiState.Success -> {
                    if (uiState.data.isEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            ErrorPlaceholder(
                                message = "This user does not have any highlights"
                            )
                        }
                    } else {
                        items(uiState.data) { edge ->
                            val node = edge.node
                            if (node != null) {
                                MediaGridItem(
                                    title = node.title ?: "",
                                    coverUrl = node.coverMedia?.thumbnailSrc ?: "",
                                    onClick = { node.id?.let { onHighlightClick(it) } }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HighlightScreenLoadingPreview() {
    StoryGhostTheme {
        HighlightContent(
            userName = "johndoe",
            uiState = UiState.Loading,
            onBackClick = {},
            onHighlightClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HighlightScreenSuccessPreview() {
    val mockEdges = arrayListOf(
        Edge(
            node = Edge.Node(
                id = "h1",
                title = "Summer",
                coverMedia = Edge.Node.CoverMedia(thumbnailSrc = "")
            )
        ),
        Edge(
            node = Edge.Node(
                id = "h2",
                title = "Work",
                coverMedia = Edge.Node.CoverMedia(thumbnailSrc = "")
            )
        )
    )
    StoryGhostTheme {
        HighlightContent(
            userName = "johndoe",
            uiState = UiState.Success(mockEdges),
            onBackClick = {},
            onHighlightClick = {}
        )
    }
}
