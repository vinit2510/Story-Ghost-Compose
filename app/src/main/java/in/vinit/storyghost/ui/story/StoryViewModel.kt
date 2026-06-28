package `in`.vinit.storyghost.ui.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.vinit.storyghost.data.local.DownloadsRepository
import `in`.vinit.storyghost.data.local.LocalDataSource
import `in`.vinit.storyghost.data.model.UserStoryResponse.Data.ReelsMedia.Item
import `in`.vinit.storyghost.data.remote.story.StoryRepository
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.util.Logger
import `in`.vinit.storyghost.util.makeApiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "StoryViewModel"

class StoryViewModel(
    private val storyRepository: StoryRepository,
    private val downloadsRepository: DownloadsRepository,
    localDataSource: LocalDataSource,
    private val logger: Logger
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Item>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Item>>> = _uiState.asStateFlow()

    val isPremium = localDataSource.isPremium()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun downloadStory(item: Item, onSuccess: () -> Unit) {
        downloadsRepository.downloadFile(item, onSuccess)
    }

    var currentPageIndex = 0

    fun loadStories(id: String, isHighlight: Boolean) {
        if (_uiState.value is UiState.Success) {
            return
        }
        viewModelScope.launch {
            _uiState.update { UiState.Loading }
            val result = makeApiCall {
                if (isHighlight) {
                    val response = storyRepository.getUserHighlights(id)
                    // Reverse the highlights as getting latest to older to match legacy behaviour
                    response.data?.reelsMedia?.getOrNull(0)?.items?.reversed() ?: emptyList()
                } else {
                    val response = storyRepository.getUserStory(id)
                    response.data?.reelsMedia?.getOrNull(0)?.items ?: emptyList()
                }
            }
            if (result is UiState.Error) {
                logger.e(
                    TAG,
                    "loadStories: Failed to load stories (isHighlight=$isHighlight) for id $id",
                    result.cause
                )
            }
            _uiState.update { result }
        }
    }
}
