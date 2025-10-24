package `in`.vinit.storyghost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.vinit.storyghost.data.model.SampleData
import `in`.vinit.storyghost.data.repository.StoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _data = MutableStateFlow<SampleData?>(null)
    val data = _data.asStateFlow()

    init {
        viewModelScope.launch {
            _data.value = repository.getData()
        }
    }
}