package `in`.vinit.storyghost.data.downloads

import `in`.vinit.storyghost.data.model.UserStoryResponse.Data.ReelsMedia.Item
import java.io.File

/**
 * Repository interface for managing downloaded story photos and videos.
 *
 * Implements Scoped Storage compliance, querying the app's dedicated downloads
 * directory and integrating with Android's [android.app.DownloadManager].
 */
interface DownloadsRepository {

    /**
     * Scans and returns all locally saved story media files sorted by date modified.
     */
    suspend fun getDownloadedFiles(): List<File>

    /**
     * Deletes a downloaded media [file] from local storage.
     *
     * @return `true` if deletion succeeded, `false` otherwise.
     */
    suspend fun deleteFile(file: File): Boolean

    /**
     * Enqueues a download for a story media [data] item via Android's DownloadManager.
     *
     * @param data The story item containing video or photo URLs and user metadata.
     * @param onSuccess Callback triggered when the download request has been successfully enqueued.
     */
    fun downloadFile(data: Item, onSuccess: () -> Unit)
}
