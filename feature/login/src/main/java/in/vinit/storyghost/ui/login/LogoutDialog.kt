package `in`.vinit.storyghost.ui.login

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import `in`.vinit.storyghost.core.ui.R
import `in`.vinit.storyghost.data.model.UserInfoResponse
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.component.PrimaryButton
import `in`.vinit.storyghost.ui.theme.StoryGhostTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun LogoutDialog(
    onLogoutSuccess: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LogoutDialogViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        when (val state = uiState) {
            is UiState.Loading -> {
                Card(
                    modifier = modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            is UiState.Success -> {
                LogoutDialogContent(
                    user = state.data,
                    onLogoutClick = {
                        viewModel.logout()
                        onLogoutSuccess()
                    },
                    onDismissRequest = onDismissRequest,
                    modifier = modifier
                )
            }

            is UiState.Error -> {
                LogoutDialogContent(
                    user = null,
                    onLogoutClick = {
                        viewModel.logout()
                        onLogoutSuccess()
                    },
                    onDismissRequest = onDismissRequest,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
fun LogoutDialogContent(
    user: UserInfoResponse?,
    onLogoutClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Text(
                text = stringResource(R.string.your_account),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            val displayUser = user?.data?.userContainer?.reel?.user

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(displayUser?.profilePicUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = displayUser?.username,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape),
                placeholder = painterResource(R.drawable.avatar_instagram_user),
                error = painterResource(R.drawable.avatar_instagram_user)
            )

            val username = displayUser?.username
            if (!username.isNullOrBlank()) {
                Text(
                    text = stringResource(R.string.insta_username, username),
                    modifier = Modifier.padding(top = 10.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            val fullName = displayUser?.fullName
            if (!fullName.isNullOrBlank()) {
                Text(
                    text = fullName,
                    modifier = Modifier.padding(top = 5.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp),
                text = stringResource(R.string.logout),
                color = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    onLogoutClick()
                    onDismissRequest()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LogoutDialogPreview() {
    StoryGhostTheme {
        LogoutDialogContent(
            user = UserInfoResponse(
                data = UserInfoResponse.Data(
                    userContainer = UserInfoResponse.Data.UserContainer(
                        reel = UserInfoResponse.Data.UserContainer.Reel(
                            user = UserInfoResponse.Data.UserContainer.Reel.User(
                                id = "1",
                                username = "instagram",
                                fullName = "Instagram User",
                                profilePicUrl = ""
                            )
                        )
                    )
                )
            ),
            onLogoutClick = {},
            onDismissRequest = {}
        )
    }
}
