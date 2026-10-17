package `in`.vinit.storyghost.ui.story

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.vinit.storyghost.core.ads.InterstitialAdManager
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.core.common.storage.LocalStorage
import `in`.vinit.storyghost.core.network.makeApiCall
import `in`.vinit.storyghost.core.play.InAppReviewManager
import `in`.vinit.storyghost.data.downloads.DownloadsRepository
import `in`.vinit.storyghost.data.model.UserStoryResponse.Data.ReelsMedia.Item
import `in`.vinit.storyghost.data.remote.config.RemoteConfigRepository
import `in`.vinit.storyghost.data.story.StoryRepository
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.toUiError
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
    localStorage: LocalStorage,
    private val logger: Logger,
    private val interstitialAdManager: InterstitialAdManager,
    private val inAppReviewManager: InAppReviewManager,
    private val remoteConfigRepository: RemoteConfigRepository
) : ViewModel() {

    var currentPageIndex = 0

    private val _uiState = MutableStateFlow<UiState<List<Item>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Item>>> = _uiState.asStateFlow()

    val isPremium = localStorage.isPremium()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun loadAd() {
        interstitialAdManager.loadAd()
    }

    fun showAd(activity: Activity) {
        interstitialAdManager.showAd(activity) { act ->
            inAppReviewManager.show(act)
        }
    }

    fun showReviewDialogIfEligible(activity: Activity) {
        viewModelScope.launch {
            inAppReviewManager.showDialogAfterDays(activity)
        }
    }

    fun getInstagramAccountUrl(): String {
        return remoteConfigRepository.getInstagramAccountUrl()
    }

    fun downloadStory(item: Item, onSuccess: () -> Unit) {
        downloadsRepository.downloadFile(item, onSuccess)
    }

    fun loadStories(storyId: String, isHighlight: Boolean) {
        if (_uiState.value is UiState.Success) {
            return
        }
        if (isHighlight) {
            loadHighlightStory(storyId)
        } else {
            loadUserStories(storyId)
        }
    }

    private fun loadUserStories(userId: String) {
        viewModelScope.launch {
            _uiState.update { UiState.Loading }
            val result = makeApiCall { storyRepository.getStoryByUserId(userId) }
            result.fold(
                onSuccess = { data ->
                    val items = data.data?.reelsMedia?.firstOrNull()?.items ?: emptyList()
                    _uiState.update { UiState.Success(items) }
                },
                onFailure = { ex ->
                    logger.e(TAG, "loadUserStories: Failed to load stories for user $userId", ex)
                    _uiState.update { ex.toUiError() }
                }
            )
        }
    }

    private fun loadHighlightStory(highlightId: String) {
        viewModelScope.launch {
            _uiState.update { UiState.Loading }
            val result = makeApiCall { storyRepository.getHighlightStoryByReelId(highlightId) }
            result.fold(
                onSuccess = { data ->
                    val items =
                        data.data?.reelsMedia?.firstOrNull()?.items?.reversed() ?: emptyList()
                    _uiState.update { UiState.Success(items) }
                },
                onFailure = { ex ->
                    logger.e(TAG, "loadHighlightStory: Failed to load highlight $highlightId", ex)
                    _uiState.update { ex.toUiError() }
                }
            )
        }
    }
}
