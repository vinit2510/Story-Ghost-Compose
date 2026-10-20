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
 * OkHttp [Interceptor] that intercepts outgoing API requests and serves pre-recorded
 * JSON fixture responses from the application's `raw` assets when mock mode is enabled.
 *
 * Essential for:
 * - Offline development and UI testing without hitting live Instagram servers.
 * - Reproducible automated unit and integration tests.
 *
 * Matches incoming GraphQL query hashes and REST endpoints to specific JSON assets:
 * - Story list: `mock_story_list.json`
 * - User stories: `mock_user_story.json`
 * - Highlights: `mock_highlights.json`
 * - Search: `mock_search_user.json`
 * - Following feed: `mock_following.json`
 */
class MockInterceptor(
    private val context: Context,
    private val appConfig: AppConfig
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url
        val path = url.encodedPath

        val mockFileName = when {
            path.contains("graphql/query") -> {
                val formBody = request.body as? okhttp3.FormBody
                val queryHash = url.queryParameter("query_hash")
                    ?: (0 until (formBody?.size ?: 0)).firstOrNull { formBody?.name(it) == "doc_id" }?.let { formBody?.value(it) }
                val variables = url.queryParameter("variables")
                    ?: (0 until (formBody?.size ?: 0)).firstOrNull { formBody?.name(it) == "variables" }?.let { formBody?.value(it) }
                    ?: ""

                when {
                    queryHash == appConfig.qhStoryList -> "mock_story_list.json"
                    queryHash == appConfig.qhHighlightList && variables.contains("\"include_highlight_reels\":true") -> "mock_highlights.json"
                    queryHash == appConfig.qhUserInfo && variables.contains("\"include_highlight_reels\":false") -> "mock_user_info.json"
                    variables.contains("highlight:") -> "mock_user_highlights.json"
                    queryHash == appConfig.docIdStoryAndHighlight -> "mock_user_story.json"
                    else -> null
                }
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

            val jsonContent = try {
                context.assets.open(mockFileName).bufferedReader().use { it.readText() }
            } catch (e: IOException) {
                "{}"
            }

            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(jsonContent.toResponseBody("application/json".toMediaTypeOrNull()))
                .build()
        }

        return chain.proceed(request)
    }
}
