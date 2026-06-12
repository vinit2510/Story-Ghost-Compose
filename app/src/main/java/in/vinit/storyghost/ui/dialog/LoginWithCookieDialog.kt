package `in`.vinit.storyghost.ui.dialog

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.vinit.storyghost.R
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.component.PrimaryButton
import `in`.vinit.storyghost.ui.onboarding.LoginViewModel
import `in`.vinit.storyghost.ui.theme.StoryGhostTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginWithCookieDialog(
    onDismissRequest: () -> Unit,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val loginSuccessfulText = stringResource(R.string.login_successful)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Success -> {
                if ((uiState as UiState.Success<Boolean>).data) {
                    Toast.makeText(context, loginSuccessfulText, Toast.LENGTH_SHORT).show()
                    viewModel.resetUiState()
                    onLoginSuccess()
                }
            }

            is UiState.Error -> {
                val errorMsg = (uiState as UiState.Error).errorMsg.asString(context)
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                viewModel.resetUiState()
            }

            else -> {}
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        LoginWithCookieDialogContent(
            uiState = uiState,
            onLoginClick = { cookieText -> viewModel.loginWithCookie(cookieText) },
            modifier = modifier
        )
    }
}

@Composable
fun LoginWithCookieDialogContent(
    uiState: UiState<Boolean>,
    onLoginClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val pleasePasteCookieText = stringResource(R.string.please_paste_cookie_first)
    var cookieText by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
    ) {
        Column(
            modifier = modifier
                .padding(15.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Text(
                text = stringResource(R.string.login_using_cookie),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = cookieText,
                    onValueChange = { cookieText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.paste_your_account_cookie_here),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    },
                    textStyle = MaterialTheme.typography.labelMedium,
                    maxLines = 12,
                    singleLine = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                IconButton(
                    onClick = {
                        scope.launch {
                            clipboard.getClipEntry()?.let { entry ->
                                entry.clipData.getItemAt(0)?.text?.toString()
                                    ?.let { cookieText = it }
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_paste),
                        contentDescription = stringResource(R.string.paste),
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = stringResource(R.string.how_to_get_cookie_watch_video),
                modifier = Modifier
                    .align(Alignment.Start)
                    .clickable {
                        openCookieVideo(context)
                    },
                style = MaterialTheme.typography.labelLarge.copy(
                    textDecoration = TextDecoration.Underline
                ),
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = stringResource(R.string.cookie_login_info),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.align(Alignment.Start)
            )

            if (uiState is UiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp
                )
            } else {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.login),
                    color = MaterialTheme.colorScheme.onPrimary,
                    onClick = {
                        if (cookieText.trim().isNotEmpty()) {
                            onLoginClick(cookieText.trim())
                        } else {
                            Toast.makeText(context, pleasePasteCookieText, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                )
            }
        }
    }
}

private fun openCookieVideo(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW)
    val url = "https://youtu.be/f_YWxX28Qi4"
    intent.data = url.toUri()
    try {
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}

@Preview
@Composable
private fun LoginWithCookieDialogPreview() {
    StoryGhostTheme {
        LoginWithCookieDialogContent(
            uiState = UiState.Success(false),
            onLoginClick = {},
        )
    }
}
