package `in`.vinit.storyghost.core.network

import android.content.Context
import `in`.vinit.storyghost.BuildConfig
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException

class MockInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url
        val path = url.encodedPath

        val mockFileName = when {
            path.contains("graphql/query") -> {
                val queryHash = url.queryParameter("query_hash")
                val variables = url.queryParameter("variables") ?: ""

                when (queryHash) {
                    BuildConfig.QH_STORY_LIST -> "mock_story_list.json"
                    BuildConfig.QH_USER_STORY -> "mock_user_story.json"
                    BuildConfig.QH_HIGHLIGHT_STORY -> "mock_user_highlights.json"
                    BuildConfig.QH_HIGHLIGHT_LIST if (variables.contains("\"include_highlight_reels\":true")) -> "mock_highlights.json"
                    BuildConfig.QH_USER_INFO if (variables.contains("\"include_highlight_reels\":false")) -> "mock_user_info.json"
                    else -> null
                }
            }

            path.contains("web/search/topsearch") -> "mock_search_user.json"
            else -> null
        }

        if (mockFileName != null) {
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
