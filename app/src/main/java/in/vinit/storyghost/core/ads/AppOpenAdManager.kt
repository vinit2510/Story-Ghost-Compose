package `in`.vinit.storyghost.core.ads

import android.app.Activity
import com.google.android.libraries.ads.mobile.sdk.appopen.AppOpenAd
import com.google.android.libraries.ads.mobile.sdk.appopen.AppOpenAdEventCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import `in`.vinit.storyghost.BuildConfig
import `in`.vinit.storyghost.core.common.Logger

private const val TAG = "AppOpenAdManager"

class AppOpenAdManager(
    private val logger: Logger,
    private val adStateManager: AdStateManager
) {
    private var appOpenAd: AppOpenAd? = null
    private var isAdLoading = false

    fun loadAd() {
        if (adStateManager.isPremium) {
            logger.d(TAG, "loadAd: Skipped because user is premium")
            return
        }
        if (adStateManager.isAnyAdShowing) {
            logger.d(TAG, "loadAd: Skipped because a fullscreen ad is currently showing")
            return
        }
        if (isAdLoading) {
            logger.d(TAG, "loadAd: Skipped because ad currently loading")
            return
        }

        if (appOpenAd != null) {
            logger.d(TAG, "loadAd: Skipped because ad is already loaded")
            return
        }

        isAdLoading = true
        logger.d(TAG, "loadAd: Starting to load App Open Ad")

        val adRequest = AdRequest.Builder(BuildConfig.ADMOB_APP_OPEN_AD_ID).build()

        AppOpenAd.load(
            adRequest,
            object : AdLoadCallback<AppOpenAd> {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    logger.e(TAG, "Admob app open onAdFailedToLoad: ${adError.message}")
                    isAdLoading = false
                    appOpenAd = null
                }

                override fun onAdLoaded(ad: AppOpenAd) {
                    logger.d(TAG, "Admob app open onAdLoaded")
                    isAdLoading = false
                    appOpenAd = ad
                }
            }
        )
    }

    fun showAd(activity: Activity) {
        if (activity.isFinishing || activity.isDestroyed) {
            logger.w(TAG, "showAd: Skipped because activity is finishing or destroyed")
            return
        }
        if (adStateManager.isPremium) {
            logger.d(TAG, "showAd: Skipped because user is premium")
            return
        }
        if (appOpenAd == null) {
            logger.w(TAG, "showAd: Skipped because appOpenAd is null")
            return
        }
        if (adStateManager.isAnyAdShowing) {
            logger.d(TAG, "showAd: Skipped because another full-screen ad is currently showing")
            return
        }

        adStateManager.onAdShown()
        logger.d(TAG, "showAd: Displaying App Open Ad")

        appOpenAd?.adEventCallback = object : AppOpenAdEventCallback {
            override fun onAdDismissedFullScreenContent() {
                logger.d(TAG, "onAdDismissedFullScreenContent: App Open Ad dismissed")
                appOpenAd = null
                adStateManager.onAdDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(fullScreenContentError: FullScreenContentError) {
                logger.e(
                    TAG,
                    "onAdFailedToShowFullScreenContent: App Open Ad failed to show: ${fullScreenContentError.message}"
                )
                appOpenAd = null
                adStateManager.onAdDismissed()
            }
        }
        appOpenAd?.show(activity)
    }
}
