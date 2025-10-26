package `in`.vinit.storyghost.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.theme.StoryGhostTheme
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = getViewModel<MainViewModel>()
            val storyList by viewModel.storyList.collectAsState()

            StoryGhostTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val text = when (val state = storyList) {
                        is UiState.Success -> state.data.toString()
                        is UiState.Loading -> "Loading"
                        is UiState.Error -> state.errorMsg.asString()
                    }

                    Greeting(
                        name = text,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StoryGhostTheme {
        Greeting("Android")
    }
}