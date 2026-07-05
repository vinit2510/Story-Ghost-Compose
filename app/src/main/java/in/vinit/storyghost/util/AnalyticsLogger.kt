package `in`.vinit.storyghost.util

import android.os.Bundle
import androidx.compose.runtime.staticCompositionLocalOf
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics

interface AnalyticsLogger {
    fun logEvent(eventName: String, params: Bundle? = null)
    fun logScreenView(screenName: String, screenClass: String? = null)
}

class FirebaseAnalyticsLogger : AnalyticsLogger {

    private val analytics: FirebaseAnalytics = Firebase.analytics

    override fun logEvent(eventName: String, params: Bundle?) {
        analytics.logEvent(eventName, params)
    }

    override fun logScreenView(screenName: String, screenClass: String?) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass ?: screenName)
        }
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }
}

val LocalAnalyticsLogger = staticCompositionLocalOf<AnalyticsLogger> {
    object : AnalyticsLogger {
        override fun logEvent(eventName: String, params: Bundle?) {}
        override fun logScreenView(screenName: String, screenClass: String?) {}
    }
}