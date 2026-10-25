package `in`.vinit.storyghost.core.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import `in`.vinit.storyghost.core.common.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * Standard Firebase Cloud Messaging service handling incoming push messages.
 *
 * Implements:
 * - Client-side constraint filtering (drops pushes silently if rules fail).
 * - Asynchronous bitmap loading for rich media.
 * - Action button and deep link intent creation.
 */
class FcmMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val notificationFilter: NotificationFilter by inject()
    private val notificationImageLoader: NotificationImageLoader by inject()
    private val notificationBuilderHelper: NotificationBuilderHelper by inject()
    private val logger: Logger by inject()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage.data
        if (data.isEmpty()) {
            logger.d(TAG, "Received empty FCM data payload, skipping.")
            return
        }

        logger.d(TAG, "Received FCM data payload: $data")
        val payload = NotificationPayload.fromData(data)

        serviceScope.launch {
            try {
                // 1. Evaluate on-device constraints
                if (!notificationFilter.shouldDisplay(payload.constraints)) {
                    logger.d(TAG, "FCM notification dropped by on-device filter.")
                    return@launch
                }

                // 2. Download rich media bitmaps
                val bigPicture = notificationImageLoader.loadBitmap(payload.imageUrl)
                val largeIcon = notificationImageLoader.loadBitmap(payload.iconUrl)

                // 3. Build and display notification
                notificationBuilderHelper.showNotification(
                    payload = payload,
                    bigPicture = bigPicture,
                    largeIcon = largeIcon
                )
            } catch (e: Exception) {
                logger.e(TAG, "Error processing incoming FCM message", e)
            }
        }
    }

    companion object {
        private const val TAG = "FcmMessagingService"
    }
}
