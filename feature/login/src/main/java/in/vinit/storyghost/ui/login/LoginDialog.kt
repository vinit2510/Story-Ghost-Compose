package `in`.vinit.storyghost.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.vinit.storyghost.core.ui.R
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.component.OpenSourceNoticeCard
import `in`.vinit.storyghost.ui.component.PrimaryButton
import `in`.vinit.storyghost.ui.theme.StoryGhostTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginDialog(
    onLoginClick: () -> Unit,
    onCookieLoginSuccess: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginDialogViewModel = koinViewModel()
) {
    val cookieMinVersionState by viewModel.cookieMinVersion.collectAsStateWithLifecycle()
    val openSourceUrl = remember { viewModel.getOpenSourceContributionUrl() }

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        val cookieMinVersion = when (val state = cookieMinVersionState) {
            is UiState.Success -> state.data
            else -> 1
        }
        val isCookieLoginEnabled = viewModel.isCookieLoginEnabled(cookieMinVersion)

        LoginDialogContent(
            isCookieLoginEnabled = isCookieLoginEnabled,
            openSourceUrl = openSourceUrl,
            onLoginClick = onLoginClick,
            onCookieLoginSuccess = {
                onDismissRequest()
                onCookieLoginSuccess()
            },
            onDismissRequest = onDismissRequest,
            modifier = modifier
        )
    }
}

@Composable
fun LoginDialogContent(
    isCookieLoginEnabled: Boolean,
    onLoginClick: () -> Unit,
    onCookieLoginSuccess: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    openSourceUrl: String = ""
) {
    val showCookieDialog = remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 15.dp)
                .padding(top = 15.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Text(
                text = stringResource(R.string.login_with_instagram),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Box(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .alpha(0.6f)
                    .background(Color(0xFFCE3B3B))
            ) {
                Text(
                    text = stringResource(R.string.instagram_account_is_required),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(top = 25.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.avatar_real_user),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 30.dp, end = 40.dp)
                            .clip(CircleShape)
                            .border(3.dp, MaterialTheme.colorScheme.outline, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = stringResource(R.string.real_account_login_msg),
                        modifier = Modifier.padding(start = 5.dp, top = 10.dp, end = 15.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }

                Image(
                    painter = painterResource(R.drawable.stroke_dotted_line_vertical),
                    contentDescription = null,
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight(),
                )

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.avatar_instagram_user),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 40.dp, end = 30.dp)
                            .clip(CircleShape)
                            .aspectRatio(1f)
                            .border(3.dp, MaterialTheme.colorScheme.outline, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = stringResource(R.string.fake_account_login_msg),
                        modifier = Modifier.padding(start = 15.dp, top = 10.dp, end = 5.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }

            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                text = stringResource(R.string.login),
                color = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    onDismissRequest()
                    onLoginClick()
                }
            )

            if (isCookieLoginEnabled) {
                Column(
                    modifier = Modifier.padding(top = 15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.width(60.dp),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = stringResource(R.string.or),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        HorizontalDivider(
                            modifier = Modifier.width(60.dp),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }

                    Text(
                        text = stringResource(R.string.login_with_cookie),
                        modifier = Modifier
                            .clickable { showCookieDialog.value = true }
                            .padding(10.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (openSourceUrl.isNotBlank()) {
                OpenSourceNoticeCard(
                    githubUrl = openSourceUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp)
                )
            }
        }
    }

    if (showCookieDialog.value) {
        LoginWithCookieDialog(
            onDismissRequest = { showCookieDialog.value = false },
            onLoginSuccess = {
                showCookieDialog.value = false
                onCookieLoginSuccess()
            }
        )
    }
}

@Preview
@Composable
private fun LoginDialogPreview() {
    StoryGhostTheme {
        LoginDialogContent(
            isCookieLoginEnabled = true,
            openSourceUrl = "https://github.com/vinit2510/Story-Ghost-Compose",
            onLoginClick = {},
            onCookieLoginSuccess = {},
            onDismissRequest = {}
        )
    }
}
