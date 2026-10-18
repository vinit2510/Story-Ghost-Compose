package `in`.vinit.storyghost.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import `in`.vinit.storyghost.core.ui.R

val DMSans = FontFamily(
    Font(R.font.dm_sans_regular, FontWeight.Normal),
    Font(R.font.dm_sans_medium, FontWeight.Medium),
    Font(R.font.dm_sans_bold, FontWeight.Bold)
)

val Typography = Typography(
    headlineMedium = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    titleSmall = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    labelLarge = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    ),
    labelMedium = TextStyle(
        fontFamily = DMSans,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = DMSans,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp
    )
)