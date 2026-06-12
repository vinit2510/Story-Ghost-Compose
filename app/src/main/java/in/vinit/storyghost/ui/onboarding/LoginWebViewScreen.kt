package `in`.vinit.storyghost.ui.onboarding

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.vinit.storyghost.ui.UiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginWebViewScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loginUrl = remember { viewModel.getInstagramLoginUrl() }
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is UiState.Success -> {
                if (state.data) {
                    viewModel.resetUiState()
                    onLoginSuccess()
                }
            }

            is UiState.Error -> {
                Toast.makeText(
                    context,
                    state.errorMsg.asString(context),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {}
        }
    }

    LoginWebViewContent(
        loginUrl = loginUrl,
        onPageFinished = { url, cookie -> viewModel.onPageFinished(url, cookie) },
        modifier = modifier
    )
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LoginWebViewContent(
    loginUrl: String,
    onPageFinished: (url: String?, cookie: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true

                // Clear cache and cookies on launch to force a fresh login screen
                clearCache(true)
                CookieManager.getInstance().removeAllCookies { }

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        val cookie = CookieManager.getInstance().getCookie(url)
                        onPageFinished(url, cookie)
                    }
                }
                loadUrl(loginUrl)
            }
        }
    )
}
