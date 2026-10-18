package `in`.vinit.storyghost.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import `in`.vinit.storyghost.ui.theme.StoryGhostTheme

@Composable
fun DashedDivider(
    color: Color,
    modifier: Modifier = Modifier,
    thickness: Dp = 2.dp,
    dashWidth: Dp = 8.dp,
    dashGap: Dp = 8.dp
) {
    Canvas(modifier = modifier.height(thickness)) {
        val dashWidthPx = dashWidth.toPx()
        val dashGapPx = dashGap.toPx()

        var x = 0f
        val y = size.height / 2

        while (x < size.width) {
            drawLine(
                color = color,
                start = Offset(x, y),
                end = Offset(x + dashWidthPx, y),
                strokeWidth = thickness.toPx()
            )
            x += dashWidthPx + dashGapPx
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashedDividerPreview() {
    StoryGhostTheme {
        DashedDivider(
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}
