package `in`.vinit.storyghost.util

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics

interface AnalyticsLogger {
    fun logEvent(eventName: String, params: Bundle? = null)
}

class FirebaseAnalyticsLogger : AnalyticsLogger {

    private val analytics: FirebaseAnalytics = Firebase.analytics

    override fun logEvent(eventName: String, params: Bundle?) {
        analytics.logEvent(eventName, params)
    }
}