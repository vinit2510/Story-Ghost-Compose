package `in`.vinit.storyghost.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.core.common.storage.LocalStorage
import `in`.vinit.storyghost.data.remote.config.RemoteConfigRepository
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "LoginViewModel"

class LoginViewModel(
    private val localStorage: LocalStorage,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val logger: Logger
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Boolean>>(UiState.Success(false))
    val uiState: StateFlow<UiState<Boolean>> = _uiState.asStateFlow()

    fun resetUiState() {
        _uiState.value = UiState.Success(false)
    }

    fun onPageFinished(url: String, cookie: String?) {
        if (!cookie.isNullOrEmpty()) {
            parseAndSaveSession(cookie)
        }
    }

    fun getInstagramLoginUrl(): String {
        return remoteConfigRepository.getInstagramLoginUrl()
    }

    fun getUserAgent(): String {
        return remoteConfigRepository.getUserAgent()
    }

    fun loginWithCookie(cookie: String) {
        try {
            val sessionId = getCookieValue(cookie, "sessionid")
            val csrfToken = getCookieValue(cookie, "csrftoken")
            val userId = getCookieValue(cookie, "ds_user_id")

            if (!sessionId.isNullOrEmpty() && !userId.isNullOrEmpty()) {
                saveSession(
                    cookie = cookie,
                    sessionId = sessionId,
                    csrfToken = csrfToken,
                    userId = userId
                )
            } else {
                _uiState.value = UiState.Error(
                    errorMsg = UiText.DynamicString("Invalid cookie format. Cookie must contain sessionid and ds_user_id.")
                )
            }
        } catch (e: Exception) {
            logger.e(TAG, "loginWithCookie failed", e)
            _uiState.value = UiState.Error(
                errorMsg = UiText.DynamicString("Failed to process cookie: ${e.localizedMessage}")
            )
        }
    }

    fun parseAndSaveSession(rawCookie: String) {
        try {
            val sessionId = getCookieValue(rawCookie, "sessionid")
            val csrfToken = getCookieValue(rawCookie, "csrftoken")
            val userId = getCookieValue(rawCookie, "ds_user_id")

            if (!sessionId.isNullOrEmpty() && !userId.isNullOrEmpty()) {
                saveSession(
                    cookie = rawCookie,
                    sessionId = sessionId,
                    csrfToken = csrfToken,
                    userId = userId
                )
            }
        } catch (e: Exception) {
            logger.e(TAG, "parseAndSaveSession failed", e)
        }
    }

    private fun saveSession(
        cookie: String?,
        sessionId: String?,
        csrfToken: String?,
        userId: String?
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // Save session details first to prevent race condition where downstream
                // listeners query the database before credentials are saved.
                cookie?.let { localStorage.saveCookie(it) }
                sessionId?.let { localStorage.saveSessionId(it) }
                csrfToken?.let { localStorage.saveCsrfToken(it) }
                userId?.let { localStorage.saveUserId(it) }
                localStorage.setLoggedIn(true)
                localStorage.setOnboarded(true)
                _uiState.value = UiState.Success(true)
            } catch (e: Exception) {
                logger.e(TAG, "Failed to save session", e)
                _uiState.value = UiState.Error(
                    errorMsg = UiText.DynamicString(
                        e.localizedMessage ?: "Failed to save login session"
                    ),
                    cause = e
                )
            }
        }
    }

    private fun getCookieValue(cookieString: String?, key: String): String? {
        if (cookieString == null) return null
        return cookieString.split(";")
            .map { it.trim() }
            .firstOrNull { it.startsWith("$key=") }
            ?.substringAfter("=")
    }
}
