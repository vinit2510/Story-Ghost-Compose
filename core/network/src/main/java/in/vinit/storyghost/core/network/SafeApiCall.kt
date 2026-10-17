package `in`.vinit.storyghost.core.network

import `in`.vinit.storyghost.core.common.network.ApiException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

/**
 * Wraps a suspending API call in a [Result], converting exceptions to failures.
 * [HttpException] is wrapped in [ApiException] to preserve HTTP status codes.
 * [CancellationException] is re-thrown to support structured concurrency.
 */
suspend fun <T> makeApiCall(call: suspend () -> T): Result<T> {
    return try {
        Result.success(call())
    } catch (e: HttpException) {
        Result.failure(ApiException(e.code(), e.message(), e))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
