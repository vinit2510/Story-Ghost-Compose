package `in`.vinit.storyghost.ui.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.vinit.storyghost.data.local.DownloadsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class DownloadsViewModel(
    private val downloadsRepository: DownloadsRepository
) : ViewModel() {

    private val _fileList = MutableStateFlow<List<File>?>(null)
    val fileList: StateFlow<List<File>?> = _fileList.asStateFlow()

    init {
        loadDownloads()
    }

    fun loadDownloads() {
        viewModelScope.launch {
            _fileList.value = downloadsRepository.getDownloadedFiles()
        }
    }

    fun deleteDownload(file: File, onDeleted: () -> Unit) {
        viewModelScope.launch {
            val success = downloadsRepository.deleteFile(file)
            if (success) {
                onDeleted()
                loadDownloads()
            }
        }
    }
}
