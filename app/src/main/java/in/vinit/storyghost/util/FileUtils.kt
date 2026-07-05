package `in`.vinit.storyghost.util

import android.content.Context
import android.content.Intent
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import `in`.vinit.storyghost.BuildConfig
import `in`.vinit.storyghost.R
import java.io.File

object FileUtils {

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
            Log.e("TAG", "openFile: ", e)
            Toast.makeText(context, R.string.toast_failed_to_open_file, Toast.LENGTH_SHORT).show()
        }
    }
}
