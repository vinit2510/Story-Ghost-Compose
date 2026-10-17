package `in`.vinit.storyghost.data.downloads

import `in`.vinit.storyghost.data.model.UserStoryResponse.Data.ReelsMedia.Item
import java.io.File

interface DownloadsRepository {
    suspend fun getDownloadedFiles(): List<File>
    suspend fun deleteFile(file: File): Boolean
    fun downloadFile(data: Item, onSuccess: () -> Unit)
}
