package `in`.vinit.storyghost.util

import com.google.gson.stream.MalformedJsonException
import `in`.vinit.storyghost.R
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.UiText
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> makeApiCall(call: suspend () -> T): UiState<T> {
    return try {
        val result = call()
        UiState.Success(result)
    } catch (e: HttpException) {
        val messageRes = when (e.code()) {
            401 -> R.string.error_unauthorized
            404 -> R.string.error_not_found
            in 500..599 -> R.string.error_server
            else -> R.string.error_unexpected
        }
        UiState.Error(UiText.StringResource(messageRes), e.code(), e)
    } catch (e: MalformedJsonException) {
        UiState.Error(UiText.StringResource(R.string.error_unexpected), null, e)
    } catch (e: javax.net.ssl.SSLException) {
        UiState.Error(UiText.StringResource(R.string.error_ssl), null, e)
    } catch (e: IOException) {
        UiState.Error(UiText.StringResource(R.string.error_network), null, e)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        UiState.Error(UiText.StringResource(R.string.error_unexpected), null, e)
    }
}
