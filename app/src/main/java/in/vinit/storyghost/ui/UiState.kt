package `in`.vinit.storyghost.ui

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(
        val errorMsg: UiText,
        val httpCode: Int? = null,
        val cause: Throwable? = null
    ) : UiState<Nothing>()
}
