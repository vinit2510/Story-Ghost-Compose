package `in`.vinit.storyghost.core.play

import android.content.Context
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.core.ui.R

private const val TAG = "InAppUpdateManager"

class InAppUpdateManager(
    private val logger: Logger
) {

    fun checkForUpdate(
        context: Context,
        resultLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        val appUpdateManager = AppUpdateManagerFactory.create(context)

        val appUpdatedListener = InstallStateUpdatedListener { installState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                Toast.makeText(context, R.string.toast_installing_update, Toast.LENGTH_SHORT).show()
                appUpdateManager.completeUpdate()
            }
        }

        appUpdateManager.registerListener(appUpdatedListener)

        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                logger.d(TAG, "In-app update available, starting update flow")
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    resultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            }
        }
    }
}
