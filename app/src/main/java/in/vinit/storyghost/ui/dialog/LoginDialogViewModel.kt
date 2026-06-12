package `in`.vinit.storyghost.ui.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.vinit.storyghost.data.remote.config.RemoteConfigRepository
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.util.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "LoginDialogViewModel"

class LoginDialogViewModel(
    private val remoteConfigRepository: RemoteConfigRepository,
    private val logger: Logger
) : ViewModel() {

    private val _cookieMinVersion = MutableStateFlow<UiState<Int>>(UiState.Loading)
    val cookieMinVersion = _cookieMinVersion.asStateFlow()

    init {
        loadCookieMinVersion()
    }

    private fun loadCookieMinVersion() {
        viewModelScope.launch {
            _cookieMinVersion.value = UiState.Loading
            val minVersion = remoteConfigRepository.getCookieMinVersion()
            _cookieMinVersion.value = UiState.Success(minVersion)
        }
    }
}



