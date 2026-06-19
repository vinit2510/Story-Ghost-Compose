package `in`.vinit.storyghost.util

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import `in`.vinit.storyghost.BuildConfig
import `in`.vinit.storyghost.R
import `in`.vinit.storyghost.data.model.UserStoryResponse
import java.io.File

private const val TAG = "FileUtils"
private const val STORY_TYPE_VIDEO = "GraphStoryVideo"

object FileUtils {

    fun downloadFile(
        context: Context,
        data: UserStoryResponse.Data.ReelsMedia.Item,
        onSuccess: () -> Unit
    ) {
        try {
            var extension = ".mp4"
            val url = if (data.typename == STORY_TYPE_VIDEO) {
                data.videoResources?.getOrNull(0)?.src
            } else {
                extension = ".jpg"
                data.displayUrl
            }

            if (url == null) {
                Toast.makeText(context, "Download url not found", Toast.LENGTH_SHORT).show()
                return
            }

            val mFolder = File(
                Environment.getExternalStorageDirectory(),
                "/Download/${context.getString(R.string.app_name)}/"
            )
            if (!mFolder.exists()) {
                mFolder.mkdirs()
            }

            val request = DownloadManager.Request(url.toUri())
            val filename = "${data.owner?.username}_story_${data.id}$extension"
            with(request) {
                setTitle("@${data.owner?.username} story")
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
            TimberLoggerImpl().e(TAG, "downloadStory: ", e)
            Toast.makeText(context, "Failed to download story", Toast.LENGTH_SHORT).show()
        }
    }

    fun listFiles(context: Context): ArrayList<File>? {
        val downloadsFolder = File(
            Environment.getExternalStorageDirectory(),
            "/Download/${context.getString(R.string.app_name)}/"
        )

        val fileList = downloadsFolder.listFiles()?.asList()
            ?.filter { it.extension == "jpg" || it.extension == "mp4" }
            ?.reversed()
            ?.let { ArrayList(it) }

        return fileList
    }

    fun getReadableFileSize(file: File): String {
        val fileSizeInBytes = file.length().toDouble()

        val fileSizeInKB = fileSizeInBytes / 1024
        val fileSizeInMB = fileSizeInKB / 1024

        return if (fileSizeInMB >= 1) {
            "%.1f MB".format(fileSizeInMB)
        } else {
            "%.1f KB".format(fileSizeInKB)
        }
    }

    fun shareFile(context: Context, file: File) {
        val sharingIntent = Intent(Intent.ACTION_SEND)

        if (file.extension == "mp4") {
            sharingIntent.type = "video/mp4"
        } else {
            sharingIntent.type = "image/jpg"
        }

        val fileUri = FileProvider.getUriForFile(
            context,
            BuildConfig.APPLICATION_ID + ".provider",
            file
        )

        sharingIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
        context.startActivity(Intent.createChooser(sharingIntent, "Share using"))
    }

    fun deleteFile(context: Context, file: File, onDelete: () -> Unit) {
        if (file.exists()) {
            if (file.delete()) {
                Toast.makeText(context, "File deleted", Toast.LENGTH_SHORT).show()
                onDelete()
            } else {
                Toast.makeText(context, "Couldn't delete file", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
        }
    }

    fun openFile(context: Context, file: File) {
        try {
            val fileUri = FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".provider",
                file
            )

            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)

            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(fileUri, mimeType)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION

            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to open file", Toast.LENGTH_SHORT).show()
        }
    }
}
