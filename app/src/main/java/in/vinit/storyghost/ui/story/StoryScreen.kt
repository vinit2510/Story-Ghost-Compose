package `in`.vinit.storyghost.ui.story

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun StoryScreen(storyId: String, modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.padding(it),
            text = storyId
        )
    }
}