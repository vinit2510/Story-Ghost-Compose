package `in`.vinit.storyghost.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.core.network.makeApiCall
import `in`.vinit.storyghost.data.local.LocalDataSource
import `in`.vinit.storyghost.data.model.UserInfoResponse
import `in`.vinit.storyghost.data.remote.user.UserRepository
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val TAG = "LogoutDialogViewModel"

class LogoutDialogViewModel(
    private val localDataSource: LocalDataSource,
    private val userRepository: UserRepository,
    private val logger: Logger
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<UserInfoResponse?>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val userJson = try {
                localDataSource.getUserData().first()
            } catch (e: Exception) {
                logger.e(TAG, "loadUserData: Failed to load user details from localDataSource", e)
                _uiState.value = UiState.Error(UiText.DynamicString(e.message ?: "Unknown error"))
                return@launch
            }

            var user = if (!userJson.isNullOrBlank()) {
                try {
                    Gson().fromJson(userJson, UserInfoResponse::class.java)
                } catch (e: Exception) {
                    logger.e(TAG, "loadUserData: Failed to parse cached user JSON", e)
                    null
                }
            } else null

            if (user == null) {
                val userId = try {
                    localDataSource.getUserId().first()
                } catch (e: Exception) {
                    logger.e(TAG, "loadUserData: Failed to load user ID from localDataSource", e)
                    _uiState.value =
                        UiState.Error(UiText.DynamicString(e.message ?: "Unknown error"))
                    return@launch
                }

                if (!userId.isNullOrBlank()) {
                    val result = makeApiCall { userRepository.getUserInfo(userId) }
                    if (result is UiState.Success) {
                        val userInfo = result.data
                        try {
                            val json = Gson().toJson(userInfo)
                            localDataSource.saveUserData(json)
                        } catch (e: Exception) {
                            logger.e(
                                TAG,
                                "loadUserData: Failed to save user data to localDataSource",
                                e
                            )
                        }
                        user = userInfo
                    } else if (result is UiState.Error) {
                        logger.e(
                            TAG,
                            "loadUserData: Failed to fetch user info for ID: $userId",
                            result.cause
                        )
                    }
                }
            }
            _uiState.value = UiState.Success(user)
        }
    }

    fun logout() {
        viewModelScope.launch {
            localDataSource.clearAuthentication()
        }
    }
}
