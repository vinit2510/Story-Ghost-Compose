package `in`.vinit.storyghost.ui.dialog

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import `in`.vinit.storyghost.R
import `in`.vinit.storyghost.data.model.UserInfoResponse
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.component.PrimaryButton
import `in`.vinit.storyghost.ui.theme.StoryGhostTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun LogoutDialog(
    onLogoutSuccess: () -> Unit,
    onDismissRequest: () -> Unit,
    viewModel: LogoutDialogViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        when (val state = uiState) {
            is UiState.Loading -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                    onDismissRequest = onDismissRequest
                )
            }

            is UiState.Error -> {
                LogoutDialogContent(
                    user = null,
                    onLogoutClick = {
                        viewModel.logout()
                        onLogoutSuccess()
                    },
                    onDismissRequest = onDismissRequest
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

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user?.user?.profilePicUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = user?.user?.username,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.tertiary, CircleShape),
                placeholder = painterResource(R.drawable.avatar_instagram_user),
                error = painterResource(R.drawable.avatar_instagram_user)
            )

            if (!user?.user?.username.isNullOrBlank()) {
                Text(
                    text = stringResource(R.string.insta_username, user.user.username),
                    modifier = Modifier.padding(top = 10.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (!user?.user?.fullName.isNullOrBlank()) {
                Text(
                    text = user.user.fullName,
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
                user = UserInfoResponse.User(
                    id = "1",
                    username = "instagram",
                    fullName = "Instagram User",
                    profilePicUrl = ""
                )
            ),
            onLogoutClick = {},
            onDismissRequest = {}
        )
    }
}
