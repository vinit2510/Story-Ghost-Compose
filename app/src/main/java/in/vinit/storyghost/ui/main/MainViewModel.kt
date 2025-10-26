package `in`.vinit.storyghost.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.vinit.storyghost.data.model.StoryListResponse
import `in`.vinit.storyghost.data.remote.story.StoryRepository
import `in`.vinit.storyghost.data.remote.user.UserRepository
import `in`.vinit.storyghost.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _storyList = MutableStateFlow<UiState<StoryListResponse>>(UiState.Loading)
    val storyList = _storyList.asStateFlow()

    init {
        _storyList.value = UiState.Loading
        fetchStories()
    }

    private fun fetchStories() {
        viewModelScope.launch {
            _storyList.value = storyRepository.getStoryList()
        }
    }

}