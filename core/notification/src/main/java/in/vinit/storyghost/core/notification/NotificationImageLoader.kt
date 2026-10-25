package `in`.vinit.storyghost.core.notification

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import `in`.vinit.storyghost.core.common.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/**
 * Downloads image bitmaps for notification big pictures and avatars.
 */
class NotificationImageLoader(
    private val logger: Logger
) {

    /**
     * Downloads and decodes a [Bitmap] from the specified HTTP/HTTPS URL.
     * Returns null if the URL is blank, invalid, or download fails.
     */
    suspend fun loadBitmap(urlString: String?): Bitmap? {
        if (urlString.isNullOrBlank()) return null

        return withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            try {
                val url = URL(urlString)
                connection = (url.openConnection() as HttpURLConnection).apply {
                    connectTimeout = TIMEOUT_MS
                    readTimeout = TIMEOUT_MS
                    doInput = true
                    instanceFollowRedirects = true
                    connect()
                }

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }
                } else {
                    logger.w(TAG, "Failed to load image, HTTP response: ${connection.responseCode}")
                    null
                }
            } catch (e: Exception) {
                logger.w(TAG, "Error downloading notification image: $urlString: ${e.message}", e)
                null
            } finally {
                connection?.disconnect()
            }
        }
    }

    companion object {
        private const val TAG = "NotificationImageLoader"
        private const val TIMEOUT_MS = 5000
    }
}
