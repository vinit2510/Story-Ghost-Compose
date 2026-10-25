package `in`.vinit.storyghost.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build

/**
 * Manages notification channels for Android O+ (API 26+).
 *
 * Plug-and-play channel creation for applications integrating push notifications.
 */
object NotificationChannels {

    const val CHANNEL_GENERAL_ID = "general"
    const val CHANNEL_PROMOTIONS_ID = "promotions"

    /**
     * Creates and registers standard notification channels with Android's [NotificationManager].
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        // 1. General Updates Channel (High Importance)
        val generalChannel = NotificationChannel(
            CHANNEL_GENERAL_ID,
            "General Updates",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Important updates, alerts, and mentions"
            enableLights(true)
            enableVibration(true)
            setSound(defaultSoundUri, audioAttributes)
        }

        // 2. Promotions & Offers Channel (Default Importance)
        val promotionsChannel = NotificationChannel(
            CHANNEL_PROMOTIONS_ID,
            "Promotions & Offers",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Special promotions, discounts, and tips"
            enableLights(true)
            enableVibration(false)
            setSound(defaultSoundUri, audioAttributes)
        }

        notificationManager.createNotificationChannels(listOf(generalChannel, promotionsChannel))
    }
}
