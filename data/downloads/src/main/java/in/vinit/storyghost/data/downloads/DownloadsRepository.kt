package `in`.vinit.storyghost.data.downloads

import java.io.File

/**
 * Repository interface for managing downloaded story photos and videos.
 *
 * Implements Scoped Storage compliance, querying the app's dedicated downloads
 * directory and integrating with Android's [android.app.DownloadManager].
 */
interface DownloadsRepository {

    /**
     * Scans and returns all locally saved media files sorted by date modified.
     */
    suspend fun getDownloadedFiles(): List<File>

    /**
     * Deletes a downloaded media [file] from local storage.
     *
     * @return `true` if deletion succeeded, `false` otherwise.
     */
    suspend fun deleteFile(file: File): Boolean

    /**
     * Enqueues a download for a media file via Android's DownloadManager.
     *
     * @param url Direct download URL for the media asset (photo or video).
     * @param filename Desired name of the downloaded file.
     * @param title Title displayed in the Android system download notification.
     * @param onSuccess Callback triggered when the download request has been successfully enqueued.
     */
    fun downloadFile(url: String, filename: String, title: String, onSuccess: () -> Unit)
}
