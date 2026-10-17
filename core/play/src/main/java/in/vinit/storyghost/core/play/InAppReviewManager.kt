package `in`.vinit.storyghost.core.play

import android.app.Activity
import com.google.android.play.core.review.ReviewManagerFactory
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.core.common.storage.LocalStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val TAG = "InAppReviewManager"

class InAppReviewManager(
    private val localStorage: LocalStorage,
    private val logger: Logger
) {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val installTime = localStorage.getInstallTime().first()
                if (installTime == 0L) {
                    logger.d(TAG, "init: First launch detected, saving install time timestamp")
                    localStorage.saveInstallTime(System.currentTimeMillis())
                }
            } catch (e: Exception) {
                logger.e(TAG, "init: Failed to initialize install time", e)
            }
        }
    }

    fun show(activity: Activity) {
        if (activity.isFinishing || activity.isDestroyed) {
            logger.w(TAG, "show: Skipped because activity is finishing or destroyed")
            return
        }
        logger.d(TAG, "show: Initiating Play In-App Review Flow request")
        val manager = ReviewManagerFactory.create(activity)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (activity.isFinishing || activity.isDestroyed) {
                logger.w(
                    TAG,
                    "show: Skipped launch because activity is finishing or destroyed after request completion"
                )
                return@addOnCompleteListener
            }
            if (task.isSuccessful) {
                logger.d(TAG, "show: Play In-App Review request successful, launching flow")
                val reviewInfo = task.result
                manager.launchReviewFlow(activity, reviewInfo)
            } else {
                logger.e(TAG, "show: Play In-App Review request failed", task.exception)
            }
        }
    }

    suspend fun showDialogAfterDays(activity: Activity) {
        if (!activity.isFinishing && !activity.isDestroyed) {
            val installTimeMillis = localStorage.getInstallTime().first()
            val reviewTries = localStorage.getReviewTries().first()
            val currentTimeMillis = System.currentTimeMillis()
            val time24Hour = 24 * 60 * 60 * 1000L
            val time5Days = 5 * 24 * 60 * 60 * 1000L
            val time10Days = 10 * 24 * 60 * 60 * 1000L

            logger.d(
                TAG,
                "showDialogAfterDays: installTime=$installTimeMillis, reviewTries=$reviewTries, currentTime=$currentTimeMillis"
            )

            if (reviewTries == 0 && currentTimeMillis - installTimeMillis >= time24Hour) {
                logger.d(TAG, "showDialogAfterDays: Triggering review try 1 (24h threshold)")
                show(activity)
                localStorage.saveReviewTries(1)
            } else if (reviewTries == 1 && currentTimeMillis - installTimeMillis >= time5Days) {
                logger.d(TAG, "showDialogAfterDays: Triggering review try 2 (5d threshold)")
                show(activity)
                localStorage.saveReviewTries(2)
            } else if (reviewTries == 2 && currentTimeMillis - installTimeMillis >= time10Days) {
                logger.d(TAG, "showDialogAfterDays: Triggering review try 3 (10d threshold)")
                show(activity)
                localStorage.saveReviewTries(3)
            }
        }
    }
}
