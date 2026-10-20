package `in`.vinit.storyghost.data.downloads

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.core.net.toUri
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.core.ui.R
import `in`.vinit.storyghost.data.model.UserStoryResponse.Data.ReelsMedia.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

private const val TAG = "DownloadsRepository"
private const val MEDIA_TYPE_VIDEO = 2

class DownloadsRepositoryImpl(
    private val context: Context,
    private val logger: Logger
) : DownloadsRepository {

    override suspend fun getDownloadedFiles(): List<File> = withContext(Dispatchers.IO) {
        val downloadsFolder = File(
            Environment.getExternalStorageDirectory(),
            "/Download/${context.getString(R.string.app_name)}/"
        )
        downloadsFolder.listFiles()?.asList()
            ?.filter { it.extension == "jpg" || it.extension == "mp4" }
            ?.reversed() ?: emptyList()
    }

    override suspend fun deleteFile(file: File): Boolean = withContext(Dispatchers.IO) {
        if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }

    override fun downloadFile(
        data: Item,
        onSuccess: () -> Unit
    ) {
        try {
            var extension = ".mp4"
            val url = if (data.mediaType == MEDIA_TYPE_VIDEO) {
                data.videoVersions?.firstOrNull()?.url
            } else {
                extension = ".jpg"
                data.imageVersions2?.candidates?.firstOrNull()?.url
            }

            if (url == null) {
                Toast.makeText(context, R.string.toast_download_url_not_found, Toast.LENGTH_SHORT)
                    .show()
                return
            }

            val mFolder = File(
                Environment.getExternalStorageDirectory(),
                "/Download/${context.getString(R.string.app_name)}/"
            )
            if (!mFolder.exists()) {
                mFolder.mkdirs()
            }

            val username = data.user?.username.orEmpty()
            val request = DownloadManager.Request(url.toUri())
            val filename = "${username}_story_${data.id}$extension"
            with(request) {
                setTitle("@$username story")
                setDescription("Downloading...")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "/${context.getString(R.string.app_name)}/$filename"
                )
            }
            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)

            onSuccess()
        } catch (e: Exception) {
            logger.e(TAG, "downloadStory: Failed to enqueue download request", e)
            Toast.makeText(context, R.string.toast_failed_to_download, Toast.LENGTH_SHORT).show()
        }
    }
}
