package `in`.vinit.storyghost.ui.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.data.downloads.DownloadsRepository
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.UiText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

private const val TAG = "DownloadsViewModel"

/**
 * ViewModel managing the offline Downloads gallery screen.
 *
 * Responsibilities:
 * - Scans device downloads directory via [DownloadsRepository] on initialization and refresh.
 * - Handles file deletion requests and updates the local state list.
 * - Drives loading, empty list, and populated grid states in [DownloadsScreen].
 */
class DownloadsViewModel(
    private val downloadsRepository: DownloadsRepository,
    private val logger: Logger
) : ViewModel() {

    private val _fileList = MutableStateFlow<UiState<List<File>>>(UiState.Loading)
    val fileList: StateFlow<UiState<List<File>>> = _fileList.asStateFlow()

    init {
        loadDownloads()
    }

    fun loadDownloads() {
        viewModelScope.launch {
            _fileList.value = UiState.Loading
            try {
                val files = downloadsRepository.getDownloadedFiles()
                _fileList.value = UiState.Success(files)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                logger.e(TAG, "loadDownloads: Failed to load downloaded files", e)
                _fileList.value =
                    UiState.Error(UiText.DynamicString(e.message ?: "Failed to load files"))
            }
        }
    }

    fun deleteDownload(file: File, onDeleted: () -> Unit) {
        viewModelScope.launch {
            val success = downloadsRepository.deleteFile(file)
            if (success) {
                onDeleted()
                loadDownloads()
            } else {
                logger.w(TAG, "deleteDownload: Failed to delete file ${file.name}")
            }
        }
    }
}
