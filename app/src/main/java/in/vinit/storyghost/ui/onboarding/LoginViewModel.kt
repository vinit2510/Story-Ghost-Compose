package `in`.vinit.storyghost.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.data.local.LocalDataSource
import `in`.vinit.storyghost.data.remote.config.RemoteConfigRepository
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "LoginViewModel"

class LoginViewModel(
    private val localDataSource: LocalDataSource,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val logger: Logger
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Boolean>>(UiState.Success(false))
    val uiState: StateFlow<UiState<Boolean>> = _uiState.asStateFlow()

    fun getInstagramLoginUrl(): String {
        return remoteConfigRepository.getInstagramLoginUrl()
    }

    fun loginWithCookie(cookie: String) {
        logger.d(TAG, "loginWithCookie: $cookie")
        try {
            val sessionId = getCookieValue(cookie, "sessionid")
            val csrfToken = getCookieValue(cookie, "csrftoken")
            val userId = getCookieValue(cookie, "ds_user_id")

            logger.d(TAG, "cookie     : $cookie")
            logger.d(TAG, "sessionid  : $sessionId")
            logger.d(TAG, "csrftoken  : $csrfToken")
            logger.d(TAG, "ds_user_id : $userId")

            if (!sessionId.isNullOrEmpty() && !userId.isNullOrEmpty()) {
                saveSession(cookie, sessionId, csrfToken, userId)
            } else {
                _uiState.value = UiState.Error(
                    errorMsg = UiText.DynamicString("Failed to login, cookie is not valid.")
                )
            }
        } catch (e: Exception) {
            logger.e(TAG, "Error validating cookie", e)
            _uiState.value = UiState.Error(
                errorMsg = UiText.DynamicString(
                    e.localizedMessage ?: "Failed to validate cookie"
                ),
                cause = e
            )
        }
    }

    fun resetUiState() {
        _uiState.value = UiState.Success(false)
    }

    fun onPageFinished(url: String?, rawCookie: String?) {
        logger.d(TAG, "onPageFinished: $url")
        try {
            val sessionId = getCookieValue(rawCookie, "sessionid")
            val csrfToken = getCookieValue(rawCookie, "csrftoken")
            val userId = getCookieValue(rawCookie, "ds_user_id")

            logger.d(TAG, "cookie     : $rawCookie")
            logger.d(TAG, "sessionid  : $sessionId")
            logger.d(TAG, "csrftoken  : $csrfToken")
            logger.d(TAG, "ds_user_id : $userId")

            if (!userId.isNullOrEmpty()) {
                saveSession(rawCookie, sessionId, csrfToken, userId)
            }
        } catch (e: Exception) {
            logger.e(TAG, "Error checking cookies", e)
            _uiState.value = UiState.Error(
                errorMsg = UiText.DynamicString(
                    e.localizedMessage ?: "Failed to extract session details"
                ),
                cause = e
            )
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
                cookie?.let { localDataSource.saveCookie(it) }
                sessionId?.let { localDataSource.saveSessionId(it) }
                csrfToken?.let { localDataSource.saveCsrfToken(it) }
                userId?.let { localDataSource.saveUserId(it) }
                localDataSource.setLoggedIn(true)
                localDataSource.setOnboarded(true)
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
