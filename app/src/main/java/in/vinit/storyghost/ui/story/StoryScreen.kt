package `in`.vinit.storyghost.ui.story

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import `in`.vinit.storyghost.ui.theme.StoryGhostTheme

@Composable
fun StoryScreen(storyId: String, modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.padding(it),
            text = storyId,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview
@Composable
private fun StoryScreenPreview() {
    StoryGhostTheme {
        StoryScreen(storyId = "cristiano")
    }
}