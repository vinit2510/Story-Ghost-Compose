package `in`.vinit.storyghost.data.downloads

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.core.net.toUri
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.core.ui.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

private const val TAG = "DownloadsRepository"

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
        url: String,
        filename: String,
        title: String,
        onSuccess: () -> Unit
    ) {
        try {
            if (url.isBlank()) {
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

            val sanitizedFilename = filename.trim().ifEmpty { "instagram" }
            val downloadTitle = title.trim().ifEmpty { sanitizedFilename }

            val request = DownloadManager.Request(url.toUri())
            with(request) {
                setTitle(downloadTitle)
                setDescription("Downloading...")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "/${context.getString(R.string.app_name)}/$sanitizedFilename"
                )
            }
            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)

            onSuccess()
        } catch (e: Exception) {
            logger.e(TAG, "downloadFile: Failed to enqueue download request", e)
            Toast.makeText(context, R.string.toast_failed_to_download, Toast.LENGTH_SHORT).show()
        }
    }
}
