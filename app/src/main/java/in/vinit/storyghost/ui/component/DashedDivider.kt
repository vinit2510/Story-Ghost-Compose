package `in`.vinit.storyghost.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DashedDivider(
    modifier: Modifier,
    color: Color,
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
