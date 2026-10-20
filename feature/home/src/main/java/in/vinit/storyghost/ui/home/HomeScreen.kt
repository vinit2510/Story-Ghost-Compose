package `in`.vinit.storyghost.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import `in`.vinit.storyghost.core.ui.R
import `in`.vinit.storyghost.data.model.StoryUser
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.component.ErrorPlaceholder
import `in`.vinit.storyghost.ui.component.MediaGridItem
import `in`.vinit.storyghost.ui.component.storyGridShimmerItems
import `in`.vinit.storyghost.ui.theme.StoryGhostTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onStoryClick: (userId: String, userName: String, isHighlight: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit = {},
    onDownloadsClick: () -> Unit = {},
    onPremiumClick: () -> Unit = {},
    renderLoginDialog: (@Composable (onDismiss: () -> Unit) -> Unit)? = null,
    renderLogoutDialog: (@Composable (onDismiss: () -> Unit, onLogout: () -> Unit) -> Unit)? = null,
    viewModel: HomeViewModel = koinViewModel()
) {
    val displayList by viewModel.displayList.collectAsStateWithLifecycle()
    val isHighlightMode by viewModel.isHighlightMode.collectAsStateWithLifecycle()
    val profilePicUrl by viewModel.profilePicUrl.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val isPremium by viewModel.isPremium.collectAsStateWithLifecycle()
    val showLoginDialogTrigger by viewModel.showLoginDialogTrigger.collectAsStateWithLifecycle()
    val isPaginating by viewModel.isPaginating.collectAsStateWithLifecycle()

    val showLoginDialog = remember { mutableStateOf(false) }
    val showLogoutDialog = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    LaunchedEffect(showLoginDialogTrigger) {
        if (showLoginDialogTrigger) {
            showLoginDialog.value = true
            viewModel.onLoginDialogTriggerHandled()
        }
    }

    if (showLoginDialog.value) {
        if (renderLoginDialog != null) {
            renderLoginDialog { showLoginDialog.value = false }
        } else {
            LaunchedEffect(Unit) {
                showLoginDialog.value = false
                onLoginClick()
            }
        }
    }

    if (showLogoutDialog.value) {
        renderLogoutDialog?.invoke(
            { showLogoutDialog.value = false },
            {
                showLogoutDialog.value = false
                viewModel.logout()
            }
        )
    }

    HomeContent(
        displayList = displayList,
        isHighlightMode = isHighlightMode,
        profilePicUrl = profilePicUrl,
        isPremium = isPremium,
        isPaginating = isPaginating,
        onHighlightToggle = viewModel::toggleHighlightMode,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSearchClick = viewModel::performSearch,
        onStoryClick = onStoryClick,
        onDownloadsClick = onDownloadsClick,
        onPremiumClick = onPremiumClick,
        onProfileClick = {
            if (isLoggedIn) {
                showLogoutDialog.value = true
            } else {
                showLoginDialog.value = true
            }
        },
        onRetryClick = { viewModel.retry() },
        onLoadMore = viewModel::loadNextPage,
        modifier = modifier
    )
}

@Composable
fun HomeContent(
    displayList: UiState<List<StoryUser>>,
    isHighlightMode: Boolean,
    profilePicUrl: String?,
    isPremium: Boolean,
    onHighlightToggle: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onStoryClick: (userId: String, userName: String, isHighlight: Boolean) -> Unit,
    onDownloadsClick: () -> Unit,
    onPremiumClick: () -> Unit,
    onProfileClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPaginating: Boolean = false,
    onLoadMore: () -> Unit = {}
) {
    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    val displayHeight = LocalWindowInfo.current.containerSize.height
    val showButton by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex > 0 || gridState.firstVisibleItemScrollOffset > displayHeight
        }
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisibleItemIndex >= totalItems - 4
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                        .clickable {
                            coroutineScope.launch {
                                gridState.animateScrollToItem(0)
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 38.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.back_to_top),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Image(
                        painter = painterResource(R.drawable.ic_scroll_top),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 10.dp)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 15.dp,
                top = innerPadding.calculateTopPadding(),
                end = 15.dp,
                bottom = innerPadding.calculateBottomPadding() + 10.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                HomeTopBar(
                    isHighlightMode = isHighlightMode,
                    profilePicUrl = profilePicUrl,
                    isPremium = isPremium,
                    isSearchActive = displayList is UiState.Loading,
                    onHighlightToggle = onHighlightToggle,
                    onSearchQueryChange = onSearchQueryChange,
                    onSearchClick = onSearchClick,
                    onDownloadsClick = onDownloadsClick,
                    onPremiumClick = onPremiumClick,
                    onProfileClick = onProfileClick
                )
            }

            when (displayList) {
                is UiState.Loading -> {
                    storyGridShimmerItems()
                }

                is UiState.Error -> {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        ErrorPlaceholder(
                            message = displayList.errorMsg.asString(),
                            onRetryClick = onRetryClick
                        )
                    }
                }

                is UiState.Success -> {
                    if (displayList.data.isEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            ErrorPlaceholder(
                                message = stringResource(R.string.no_result_found)
                            )
                        }
                    } else {
                        items(displayList.data, key = { it.id ?: it.hashCode() }) { user ->

                            val username = user.username ?: ""
                            val profilePicUrl = user.profilePicUrl ?: ""

                            MediaGridItem(
                                title = username,
                                coverUrl = profilePicUrl,
                                onClick = {
                                    onStoryClick(
                                        user.id ?: "",
                                        username,
                                        isHighlightMode
                                    )
                                },
                                prefixUsername = true,
                                isVerified = user.isVerified
                            )
                        }

                        if (isPaginating) {
                            val shimmerCount = displayList.data.size % 2 + 2
                            storyGridShimmerItems(count = shimmerCount)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeTopBar(
    isHighlightMode: Boolean,
    profilePicUrl: String?,
    isPremium: Boolean,
    isSearchActive: Boolean,
    onHighlightToggle: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onDownloadsClick: () -> Unit,
    onPremiumClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchState = rememberTextFieldState(initialText = "")
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(searchState) {
        snapshotFlow { searchState.text.toString() }
            .collect { onSearchQueryChange(it) }
    }

    var isShowingAppIcon by remember { mutableStateOf(true) }

    LaunchedEffect(isPremium) {
        if (isPremium) {
            isShowingAppIcon = true
        } else {
            while (true) {
                delay(5000)
                isShowingAppIcon = !isShowingAppIcon
            }
        }
    }

    Column(
        modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AnimatedContent(
                targetState = isShowingAppIcon,
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)) togetherWith fadeOut(
                        animationSpec = tween(
                            500
                        )
                    )
                },
                label = "AppIconAnimation"
            ) { showingAppIcon ->
                val resId =
                    if (showingAppIcon) R.drawable.ic_app_logo_round else R.drawable.ic_premium_lock_round
                Image(
                    painter = painterResource(resId),
                    contentDescription = "App Icon",
                    modifier = Modifier.size(48.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
                Text(
                    stringResource(R.string.app_name),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(3.dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (isPremium) MaterialTheme.colorScheme.tertiary
                            else MaterialTheme.colorScheme.primary
                        )
                        .clickable {
                            if (!isPremium) {
                                onPremiumClick()
                            }
                        }
                ) {
                    Text(
                        text = if (isPremium) stringResource(R.string.plan_active) else stringResource(
                            R.string.buy_premium
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.inversePrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Card(
                onClick = onDownloadsClick,
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.size(40.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painterResource(R.drawable.ic_downloads),
                        contentDescription = "Downloads"
                    )
                }
            }

            val context = LocalContext.current
            val profileImageRequest = remember(profilePicUrl, context) {
                ImageRequest.Builder(context)
                    .data(profilePicUrl)
                    .crossfade(true)
                    .build()
            }
            AsyncImage(
                model = profileImageRequest,
                contentDescription = "Profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onProfileClick() },
                placeholder = painterResource(R.drawable.avatar_instagram_user),
                error = painterResource(R.drawable.avatar_instagram_user)
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 30.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                state = searchState,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape),
                textStyle = MaterialTheme.typography.titleSmall,
                placeholder = {
                    Text(
                        if (isHighlightMode)
                            stringResource(R.string.search_any_user_highlights)
                        else
                            stringResource(R.string.search_username_name),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                },
                trailingIcon = {
                    val isSearchClickable =
                        searchState.text.trim().length >= SEARCH_THRESHOLD && !isSearchActive
                    IconButton(
                        onClick = {
                            keyboardController?.hide()
                            onSearchClick()
                        },
                        enabled = isSearchClickable
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = "Search",
                            tint = if (isSearchClickable)
                                MaterialTheme.colorScheme.onBackground
                            else
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                onKeyboardAction = {
                    if (searchState.text.trim().length >= SEARCH_THRESHOLD && !isSearchActive) {
                        keyboardController?.hide()
                        onSearchClick()
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = if (isHighlightMode)
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = if (isHighlightMode)
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                lineLimits = TextFieldLineLimits.SingleLine,
                shape = MaterialTheme.shapes.small,
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp)
                .clickable { onHighlightToggle() },
            shape = MaterialTheme.shapes.small,
            colors = CardDefaults.cardColors(
                containerColor = if (isHighlightMode)
                    MaterialTheme.colorScheme.secondary
                else
                    MaterialTheme.colorScheme.tertiary
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_highlight),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(R.string.find_highlights),
                    modifier = Modifier.padding(start = 6.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        AnimatedVisibility(
            visible = isHighlightMode,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = stringResource(R.string.highlight_desc),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Preview
@Composable
private fun HomeContentPreview() {
    StoryGhostTheme {
        HomeContent(
            displayList = UiState.Success(
                listOf(
                    StoryUser("1", "cristiano", ""),
                    StoryUser("2", "leomessi", ""),
                    StoryUser("3", "neymarjr", ""),
                    StoryUser("4", "taylorswift", "")
                )
            ),
            isHighlightMode = false,
            profilePicUrl = null,
            isPremium = false,
            onHighlightToggle = {},
            onSearchQueryChange = {},
            onSearchClick = {},
            onStoryClick = { _, _, _ -> },
            onDownloadsClick = {},
            onPremiumClick = {},
            onProfileClick = {},
            onRetryClick = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}