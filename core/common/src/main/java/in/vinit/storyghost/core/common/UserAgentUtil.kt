package `in`.vinit.storyghost.core.common

import android.content.Context
import android.os.Build
import android.webkit.WebSettings
import `in`.vinit.storyghost.core.common.UserAgentUtil.FALLBACK_USER_AGENT
import java.util.Locale

/**
 * Utility for synthesizing realistic Instagram Android app and browser User-Agent strings.
 *
 * Essential for GraphQL and feed API requests to prevent bot detection and challenge interstitials.
 */
object UserAgentUtil {

    private const val FALLBACK_USER_AGENT =
        "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Mobile Safari/537.36"

    /**
     * Synthesizes a valid Instagram Mobile user agent string reflecting the host device's
     * screen metrics, brand, model, CPU hardware, and Android SDK version.
     *
     * Falls back to [FALLBACK_USER_AGENT] if context resolution fails.
     */
    fun generateInstagramUserAgent(context: Context): String {
        return try {
            val defaultAgent = WebSettings.getDefaultUserAgent(context)

            val sdkInt = Build.VERSION.SDK_INT
            val release = Build.VERSION.RELEASE

            val metrics = context.resources.displayMetrics
            val dpi = metrics.densityDpi
            val width = metrics.widthPixels
            val height = metrics.heightPixels

            val brand = Build.BRAND
            val model = Build.MODEL
            val board = Build.BOARD
            val hardware = Build.HARDWARE

            val locale = Locale.getDefault().toString()

            val instagramVersion = "436.0.0.41.73"
            val buildNumber = "1006556549"

            val suffix =
                " Instagram $instagramVersion Android ($sdkInt/$release; ${dpi}dpi; ${width}x${height}; $brand; $model; $board; $hardware; $locale; $buildNumber; IABMV/1)"

            "$defaultAgent$suffix"
        } catch (e: Exception) {
            FALLBACK_USER_AGENT
        }
    }
}
