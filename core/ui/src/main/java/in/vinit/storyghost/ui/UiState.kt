package `in`.vinit.storyghost.ui

import `in`.vinit.storyghost.core.common.network.ApiException
import java.io.IOException
import javax.net.ssl.SSLException

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(
        val errorMsg: UiText,
        val httpCode: Int? = null,
        val cause: Throwable? = null
    ) : UiState<Nothing>()
}

/**
 * Converts a [Result] from the data layer into a [UiState] for the UI layer.
 * Maps known exception types to user-friendly error messages.
 */
fun <T> Result<T>.toUiState(): UiState<T> = fold(
    onSuccess = { UiState.Success(it) },
    onFailure = { e ->
        when (e) {
            is ApiException -> UiState.Error(
                errorMsg = UiText.DynamicString(e.message ?: "Unexpected error"),
                httpCode = e.httpCode,
                cause = e
            )

            is SSLException -> UiState.Error(
                errorMsg = UiText.DynamicString("SSL connection error"),
                cause = e
            )

            is IOException -> UiState.Error(
                errorMsg = UiText.DynamicString("Network connection error"),
                cause = e
            )

            else -> UiState.Error(
                errorMsg = UiText.DynamicString(e.message ?: "Unknown error"),
                cause = e
            )
        }
    }
)
