package `in`.vinit.storyghost.data.remote.auth

import `in`.vinit.storyghost.data.local.LocalDataSource
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class LoginInterceptor(private val localDataSource: LocalDataSource) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        val cookie = runBlocking { localDataSource.getCookie().firstOrNull() }
        val csrfToken = runBlocking { localDataSource.getCsrfToken().firstOrNull() }
        //TODO: val userAgent = RemoteConfigUtil.getInstance().getString(RemoteKey.USER_AGENT)
        val userAgent = ""

        cookie?.let { requestBuilder.addHeader("Cookie", it.sanitizeHeader()) }
        csrfToken?.let { requestBuilder.addHeader("X-Csrftoken", it.sanitizeHeader()) }
        requestBuilder.addHeader("User-Agent", userAgent.sanitizeHeader())

        return chain.proceed(requestBuilder.build())
    }

    private fun String.sanitizeHeader(): String {
        return this.replace(Regex("[^\\x20-\\x7E]"), "")
    }
}