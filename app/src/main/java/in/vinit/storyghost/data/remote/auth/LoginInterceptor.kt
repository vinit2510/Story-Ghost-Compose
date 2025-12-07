package `in`.vinit.storyghost.data.remote.auth

import `in`.vinit.storyghost.data.local.LocalDataSource
import `in`.vinit.storyghost.data.remote.config.RemoteConfigRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class LoginInterceptor(
    private val localDataSource: LocalDataSource,
    private val remoteConfigRepository: RemoteConfigRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        val cookie = runBlocking { localDataSource.getCookie().firstOrNull() }
        val csrfToken = runBlocking { localDataSource.getCsrfToken().firstOrNull() }
        val userAgent = remoteConfigRepository.getUserAgent()

        cookie?.let { requestBuilder.addHeader("Cookie", it.sanitizeHeader()) }
        csrfToken?.let { requestBuilder.addHeader("X-Csrftoken", it.sanitizeHeader()) }
        requestBuilder.addHeader("User-Agent", userAgent.sanitizeHeader())

        return chain.proceed(requestBuilder.build())
    }

    private fun String.sanitizeHeader(): String {
        return this.replace(Regex("[^\\x20-\\x7E]"), "")
    }
}