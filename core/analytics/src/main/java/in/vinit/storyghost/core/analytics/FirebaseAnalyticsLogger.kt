package `in`.vinit.storyghost.core.analytics

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics

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
