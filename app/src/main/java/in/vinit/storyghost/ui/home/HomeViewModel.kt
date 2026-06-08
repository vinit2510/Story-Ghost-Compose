package `in`.vinit.storyghost.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    private val _storyList = MutableStateFlow((0..100).map { "Story $it" })
    val storyList = _storyList.asStateFlow()

}