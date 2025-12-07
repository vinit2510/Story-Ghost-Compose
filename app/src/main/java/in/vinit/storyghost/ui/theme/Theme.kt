package `in`.vinit.storyghost.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Lime60,
    onPrimary = Grey10,

    secondary = Blue60,
    onSecondary = Grey95,

    background = Grey10,
    onBackground = Grey95,

    surface = Grey30,
    onSurface = Grey95,

    surfaceVariant = Grey10A80,
    onSurfaceVariant = Grey95,

    outline = Grey30A20,
    tertiary = Grey70
)

@Composable
fun StoryGhostTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}