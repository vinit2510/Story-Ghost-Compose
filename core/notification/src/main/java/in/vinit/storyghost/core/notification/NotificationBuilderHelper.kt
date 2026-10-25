package `in`.vinit.storyghost.core.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri

/**
 * Builds and posts Android notifications using [NotificationCompat.Builder].
 */
class NotificationBuilderHelper(
    private val context: Context,
    private val config: NotificationConfig
) {

    /**
     * Builds and posts the notification to [NotificationManagerCompat].
     */
    fun showNotification(
        payload: NotificationPayload,
        bigPicture: Bitmap? = null,
        largeIcon: Bitmap? = null,
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        val channelId = payload.channelId.ifBlank {
            config.defaultChannelId
        }

        val mainPendingIntent = createPendingIntent(payload.launchUrl, requestCode = notificationId)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(config.defaultSmallIconRes)
            .setContentTitle(payload.title)
            .setContentText(payload.body)
            .setContentIntent(mainPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        // Large Icon (Avatar on the right)
        if (largeIcon != null) {
            builder.setLargeIcon(largeIcon)
        }

        // Big Picture Style (Expanded Rich Banner)
        if (bigPicture != null) {
            builder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bigPicture)
                    .bigLargeIcon(null as Bitmap?)
            )
        }

        // Action Buttons (Up to 2)
        payload.actions.take(2).forEachIndexed { index, action ->
            val actionPendingIntent = createPendingIntent(
                url = action.url ?: payload.launchUrl,
                requestCode = notificationId + index + 1
            )
            builder.addAction(
                config.defaultSmallIconRes,
                action.title,
                actionPendingIntent
            )
        }

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Handled if POST_NOTIFICATIONS is not granted on Android 13+
        }
    }

    private fun createPendingIntent(url: String?, requestCode: Int): PendingIntent {
        val intent = if (!url.isNullOrBlank()) {
            Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            } ?: Intent()
        }

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getActivity(context, requestCode, intent, flags)
    }
}
