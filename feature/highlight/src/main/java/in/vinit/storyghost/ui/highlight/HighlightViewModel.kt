package `in`.vinit.storyghost.ui.highlight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.core.network.makeApiCall
import `in`.vinit.storyghost.data.model.HighlightsResponse.Data.User.EdgeHighlightReels.Edge
import `in`.vinit.storyghost.data.story.StoryRepository
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "HighlightViewModel"

/**
 * ViewModel managing the highlight reels screen for an individual user profile.
 *
 * Responsibilities:
 * - Fetches highlight collections and covers via [StoryRepository.getHighlightReelsByUserId].
 * - Avoids duplicate network calls if the highlights list is already populated in [_uiState].
 * - Emits [UiState.Loading], [UiState.Success], or [UiState.Error] to drive Compose UI states.
 */
class HighlightViewModel(
    private val storyRepository: StoryRepository,
    private val logger: Logger
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Edge>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Edge>>> = _uiState.asStateFlow()

    fun loadHighlights(userId: String) {
        if (_uiState.value is UiState.Success) {
            return
        }
        viewModelScope.launch {
            _uiState.update { UiState.Loading }
            val result = makeApiCall {
                storyRepository.getHighlightReelsByUserId(userId)
                    .data?.user?.edgeHighlightReels?.edges ?: emptyList()
            }
            if (result.isFailure) {
                logger.e(
                    TAG,
                    "loadHighlights: Failed to load highlights for user $userId",
                    result.exceptionOrNull()
                )
            }
            _uiState.update { result.toUiState() }
        }
    }
}
