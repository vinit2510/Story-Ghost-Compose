package `in`.vinit.storyghost.core.network

import android.content.Context
import `in`.vinit.storyghost.config.AppConfig
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException

/**
 * Interceptor that returns local mock JSON assets for testing and offline development.
 */
class MockInterceptor(
    private val context: Context,
    private val appConfig: AppConfig
) : Interceptor {

    private fun loadJsonFromAsset(fileName: String): String {
        return try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (_: IOException) {
            """{"status": "fail"}"""
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url
        val path = url.encodedPath

        val mockFileName = when {
            path.contains("highlights_tray") -> "mock_highlights.json"
            path.contains("users") && path.contains("info") -> "mock_user_info.json"
            path.contains("feed/reels_media") -> {
                val reelIds = url.queryParameter("reel_ids") ?: ""
                if (reelIds.contains("highlight:")) "mock_user_highlights.json" else "mock_user_story.json"
            }
            path.contains("api/v1/fbsearch/topsearch_flat") || path.contains("web/search/topsearch") -> "mock_search_user.json"
            path.contains("friendships") && path.contains("following") -> "mock_following.json"
            else -> null
        }

        if (mockFileName != null) {
            val cookieHeader = request.header("Cookie")
            val isValidCookie = !cookieHeader.isNullOrEmpty() &&
                    cookieHeader.contains("sessionid=") &&
                    cookieHeader.contains("ds_user_id=")

            if (!isValidCookie) {
                val unauthorizedJson = """{"message":"Please log in to get data","status":"fail"}"""
                return Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(401)
                    .message("Unauthorized")
                    .body(unauthorizedJson.toResponseBody("application/json".toMediaTypeOrNull()))
                    .build()
            }

            val mockResponse = loadJsonFromAsset(mockFileName)
            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(mockResponse.toResponseBody("application/json".toMediaTypeOrNull()))
                .build()
        }

        return chain.proceed(request)
    }
}
