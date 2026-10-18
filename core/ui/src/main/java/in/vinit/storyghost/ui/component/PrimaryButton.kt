package `in`.vinit.storyghost.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import `in`.vinit.storyghost.core.ui.R
import `in`.vinit.storyghost.ui.theme.StoryGhostTheme

@Composable
fun PrimaryButton(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 13.dp),
        elevation = ButtonDefaults.buttonElevation(1.dp),
        onClick = { onClick() }
    ) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Icon(
                painter = painterResource(R.drawable.ic_arrow_continue),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterEnd),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview
@Composable
private fun PrimaryButtonPreview() {
    StoryGhostTheme {
        PrimaryButton(
            text = "Button",
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        )
    }
}