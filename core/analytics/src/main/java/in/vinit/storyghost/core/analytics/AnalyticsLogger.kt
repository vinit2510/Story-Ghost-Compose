package `in`.vinit.storyghost.core.analytics

import android.os.Bundle

interface AnalyticsLogger {
    fun logEvent(eventName: String, params: Bundle? = null)
    fun logScreenView(screenName: String, screenClass: String? = null)
}
