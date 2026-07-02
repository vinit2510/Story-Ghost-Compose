package `in`.vinit.storyghost.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import `in`.vinit.storyghost.ui.theme.StoryGhostTheme
import `in`.vinit.storyghost.util.InAppUpdate

class MainActivity : ComponentActivity() {

    private val updateLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check for Google Play in-app updates
        InAppUpdate.check(this, updateLauncher)

        setContent {
            StoryGhostTheme {
                App(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}