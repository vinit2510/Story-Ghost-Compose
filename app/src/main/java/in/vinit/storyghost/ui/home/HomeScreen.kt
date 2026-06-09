package `in`.vinit.storyghost.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.vinit.storyghost.R
import `in`.vinit.storyghost.ui.theme.StoryGhostTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onStoryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel()
) {
    val storyList by viewModel.storyList.collectAsStateWithLifecycle()
    HomeContent(
        storyList = storyList,
        onStoryClick = onStoryClick,
        modifier = modifier
    )
}

@Composable
fun HomeContent(
    storyList: List<String>,
    onStoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding
        ) {
            item {
                HomeTopBar()
            }
            items(storyList) { story ->
                Text(
                    text = story,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onStoryClick(story)
                        }
                        .padding(15.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 20.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painterResource(R.drawable.ic_app_logo_round),
                "",
                modifier = Modifier.size(40.dp)
            )

            Text(
                stringResource(R.string.app_name),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Image(
                    painterResource(R.drawable.ic_downloads),
                    "Downloads",
                    modifier = Modifier.padding(8.dp)
                )
            }

            Image(
                painterResource(R.drawable.avatar_instagram_user),
                "Profile",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                state = rememberTextFieldState(initialText = ""),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                textStyle = MaterialTheme.typography.bodyMedium,
                contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
                    top = 6.dp,
                    bottom = 6.dp
                ),
                placeholder = {
                    Text(
                        stringResource(R.string.search_username_name),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                },
                leadingIcon = {
                    Icon(
                        painterResource(R.drawable.ic_search),
                        "Search",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                lineLimits = TextFieldLineLimits.SingleLine,
                shape = MaterialTheme.shapes.small,
            )
        }
    }
}

@Preview
@Composable
private fun HomeContentPreview() {
    StoryGhostTheme {
        HomeContent(
            storyList = listOf(
                "cristiano",
                "leomessi",
                "neymarjr",
                "taylorswift",
                "arianagrande",
                "selenagomez"
            ),
            onStoryClick = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}