package `in`.vinit.storyghost.core.network

import android.content.Context
import `in`.vinit.storyghost.core.common.UserAgentUtil
import `in`.vinit.storyghost.core.common.storage.LocalStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class LoginInterceptor(
    private val localStorage: LocalStorage,
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        val cookie = runBlocking {
            try {
                localStorage.getCookie().first()
            } catch (_: Exception) {
                null
            }
        }
        val csrfToken = runBlocking {
            try {
                localStorage.getCsrfToken().first()
            } catch (_: Exception) {
                null
            }
        }
        val userAgent = UserAgentUtil.generateInstagramUserAgent(context)

        if (!cookie.isNullOrEmpty()) {
            requestBuilder.header("Cookie", cookie.sanitizeHeader())
        }
        if (!csrfToken.isNullOrEmpty()) {
            requestBuilder.header("X-Csrftoken", csrfToken.sanitizeHeader())
        }
        requestBuilder.header("User-Agent", userAgent.sanitizeHeader())

        val response = chain.proceed(requestBuilder.build())

        if (response.code == 401 || response.code == 403) {
            runBlocking {
                try {
                    localStorage.clearAuthentication()
                } catch (_: Exception) {
                }
            }
        }

        return response
    }

    private fun String.sanitizeHeader(): String {
        return this.replace(Regex("[^\\x20-\\x7E]"), "")
    }
}
