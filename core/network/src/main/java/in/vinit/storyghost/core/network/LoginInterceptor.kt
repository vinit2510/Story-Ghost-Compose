package `in`.vinit.storyghost.core.network

import android.content.Context
import `in`.vinit.storyghost.core.common.UserAgentUtil
import `in`.vinit.storyghost.core.common.storage.LocalStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Base64

private const val IG_APP_ID = "1217981644879628"
private const val IG_CAPABILITIES = "360f/7f"

/**
 * OkHttp [Interceptor] that injects authenticated session headers into outgoing network requests.
 *
 * Responsibilities:
 * - Injects the active session cookie (`Cookie`), CSRF token (`X-Csrftoken`), generated Instagram mobile `User-Agent`,
 *   and synthesized Instagram `Authorization: Bearer IGT:2:...` header for mobile endpoints.
 * - Sets the official Instagram Mobile App ID header (`X-IG-App-ID`) and capabilities (`X-IG-Capabilities`).
 * - Sanitizes headers against non-ASCII characters to prevent OkHttp IllegalArgumentExceptions.
 * - Detects unauthorized responses (401) and automatically clears local session data.
 */
class LoginInterceptor(
    private val localStorage: LocalStorage,
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()

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
        val userId = runBlocking {
            try {
                localStorage.getUserId().first()
            } catch (_: Exception) {
                null
            }
        }
        val sessionId = runBlocking {
            try {
                localStorage.getSessionId().first()
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
        requestBuilder.header("X-IG-App-ID", IG_APP_ID)
        requestBuilder.header("X-IG-Capabilities", IG_CAPABILITIES)

        if (!userId.isNullOrEmpty() && !sessionId.isNullOrEmpty()) {
            try {
                val authJson = """{"ds_user_id":"$userId","sessionid":"$sessionId"}"""
                val encoded = Base64.getEncoder().encodeToString(authJson.toByteArray(Charsets.UTF_8))
                requestBuilder.header("Authorization", "Bearer IGT:2:$encoded")
            } catch (_: Exception) {
            }
        }

        val response = chain.proceed(requestBuilder.build())

        if (response.code == 401) {
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
