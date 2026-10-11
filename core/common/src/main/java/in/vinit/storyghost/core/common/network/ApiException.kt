package `in`.vinit.storyghost.core.common.network

/**
 * Exception class for API errors that preserves the HTTP status code.
 * Used by [makeApiCall] to wrap HTTP exceptions with a simpler type
 * that doesn't depend on Retrofit types downstream.
 */
class ApiException(
    val httpCode: Int,
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
