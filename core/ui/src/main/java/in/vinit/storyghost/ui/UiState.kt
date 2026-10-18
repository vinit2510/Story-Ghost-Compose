package `in`.vinit.storyghost.ui

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import `in`.vinit.storyghost.core.common.network.ApiException
import `in`.vinit.storyghost.core.ui.R
import java.io.IOException
import javax.net.ssl.SSLException

/**
 * Represents the state of a UI screen or component in Unidirectional Data Flow.
 *
 * Annotated with [Stable] and [Immutable] to enable Compose compiler smart recomposition skipping.
 *
 * @param T The type of data contained within the [Success] state.
 */
@Stable
sealed class UiState<out T> {
    /** Indicates an operation is in progress and placeholder/shimmer UI should be displayed. */
    @Immutable
    object Loading : UiState<Nothing>()

    /** Indicates the operation succeeded with the resulting [data]. */
    @Immutable
    data class Success<T>(val data: T) : UiState<T>()

    /**
     * Indicates an operation failed with a user-facing [errorMsg], optional [httpCode],
     * and underlying [cause].
     */
    @Immutable
    data class Error(
        val errorMsg: UiText,
        val httpCode: Int? = null,
        val cause: Throwable? = null
    ) : UiState<Nothing>()
}

/**
 * Maps a [Throwable] to a [UiState.Error] with localized string resources.
 */
fun Throwable.toUiError(): UiState.Error = when (this) {
    is ApiException -> {
        val messageRes = when (httpCode) {
            401 -> R.string.error_unauthorized
            404 -> R.string.error_not_found
            in 500..599 -> R.string.error_server
            else -> R.string.error_unexpected
        }
        UiState.Error(
            errorMsg = UiText.StringResource(messageRes),
            httpCode = httpCode,
            cause = this
        )
    }

    is SSLException -> UiState.Error(
        errorMsg = UiText.StringResource(R.string.error_ssl),
        cause = this
    )

    is IOException -> UiState.Error(
        errorMsg = UiText.StringResource(R.string.error_network),
        cause = this
    )

    else -> UiState.Error(
        errorMsg = message?.takeIf { it.isNotBlank() }
            ?.let { UiText.DynamicString(it) }
            ?: UiText.StringResource(R.string.error_unexpected),
        cause = this
    )
}

/**
 * Converts a [Result] from the data layer into a [UiState] for the UI layer.
 * Maps known exception types to user-friendly error messages.
 */
fun <T> Result<T>.toUiState(): UiState<T> = fold(
    onSuccess = { UiState.Success(it) },
    onFailure = { it.toUiError() }
)
