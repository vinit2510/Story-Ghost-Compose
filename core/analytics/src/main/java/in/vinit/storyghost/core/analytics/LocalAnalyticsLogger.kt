package `in`.vinit.storyghost.core.analytics

import android.os.Bundle
import androidx.compose.runtime.staticCompositionLocalOf

val LocalAnalyticsLogger = staticCompositionLocalOf<AnalyticsLogger> {
    object : AnalyticsLogger {
        override fun logEvent(eventName: String, params: Bundle?) {}
        override fun logScreenView(screenName: String, screenClass: String?) {}
    }
}
