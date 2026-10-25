package `in`.vinit.storyghost.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.navigation.NavigationRoot
import `in`.vinit.storyghost.ui.navigation.Route
import org.koin.androidx.compose.koinViewModel

/**
 * Top-level root Composable of the application.
 *
 * Observes the startup destination calculated by [StartupViewModel] (evaluating
 * force update flags, onboarding completion, and login state) and conditionally renders
 * the main [NavigationRoot] with the resolved initial route.
 *
 * @param modifier The layout modifier applied to the root container.
 * @param viewModel Injected [StartupViewModel] handling application startup logic.
 */
@Composable
fun App(
    modifier: Modifier = Modifier,
    deepLinkRoute: Route? = null,
    onDeepLinkConsumed: () -> Unit = {},
    viewModel: StartupViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is UiState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            )
        }

        is UiState.Success -> {
            NavigationRoot(
                initialRoute = state.data,
                deepLinkRoute = deepLinkRoute,
                onDeepLinkConsumed = onDeepLinkConsumed,
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            )
        }

        is UiState.Error -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            )
        }
    }
}
