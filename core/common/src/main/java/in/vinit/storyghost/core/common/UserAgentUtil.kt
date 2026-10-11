package `in`.vinit.storyghost.core.common

import android.content.Context
import android.os.Build
import android.webkit.WebSettings
import java.util.Locale

object UserAgentUtil {

    private const val FALLBACK_USER_AGENT =
        "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Mobile Safari/537.36"

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
