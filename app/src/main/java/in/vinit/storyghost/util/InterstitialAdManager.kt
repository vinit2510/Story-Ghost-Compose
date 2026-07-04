package `in`.vinit.storyghost.util

import android.app.Activity
import android.content.Context
import com.facebook.ads.Ad
import com.facebook.ads.RewardedInterstitialAdListener
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdEventCallback
import `in`.vinit.storyghost.BuildConfig
import com.facebook.ads.AdError as FANAdError
import com.facebook.ads.RewardedInterstitialAd as FANRewardedInterstitialAd

private const val TAG = "InterstitialAdManager"

class InterstitialAdManager(
    private val context: Context,
    private val logger: Logger,
    private val adStateManager: AdStateManager
) {

    private var admobInterstitialAd: InterstitialAd? = null
    private var fanRewardedInterstitialAd: FANRewardedInterstitialAd? = null
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
            logger.d(TAG, "loadAd: Skipped because ad is currently loading")
            return
        }
        if (admobInterstitialAd != null || fanRewardedInterstitialAd?.isAdLoaded == true) {
            logger.d(TAG, "loadAd: Skipped because an ad is already loaded")
            return
        }

        logger.d(TAG, "loadAd: Initiating ad load")
        loadAdmobAd()
    }

    private fun loadAdmobAd() {
        logger.d(TAG, "loadAdmobAd: Starting to load Admob Interstitial")
        isAdLoading = true

        val adRequest = AdRequest.Builder(BuildConfig.ADMOB_INTERSTITIAL_AD_ID).build()

        InterstitialAd.load(
            adRequest,
            object : AdLoadCallback<InterstitialAd> {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    logger.e(TAG, "Admob onAdFailedToLoad: ${adError.message}")
                    isAdLoading = false
                    admobInterstitialAd = null
                    loadFanAd()
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    logger.d(TAG, "Admob onAdLoaded")
                    isAdLoading = false
                    admobInterstitialAd = ad
                }
            }
        )
    }

    private fun loadFanAd() {
        logger.d(TAG, "loadFanAd: Starting to load FAN Rewarded Interstitial")
        isAdLoading = true

        fanRewardedInterstitialAd =
            FANRewardedInterstitialAd(
                context.applicationContext,
                BuildConfig.FAN_REWARDED_INTERSTITIAL_AD_ID
            )

        fanRewardedInterstitialAd?.let {
            it.loadAd(
                it.buildLoadAdConfig().withAdListener(object : RewardedInterstitialAdListener {
                    override fun onError(ad: Ad?, error: FANAdError?) {
                        logger.e(TAG, "FAN onError: ${error?.errorMessage}")
                        isAdLoading = false
                        adStateManager.onAdDismissed()
                    }

                    override fun onAdLoaded(ad: Ad?) {
                        logger.d(TAG, "FAN onAdLoaded")
                        isAdLoading = false
                    }

                    override fun onAdClicked(ad: Ad?) {
                        logger.d(TAG, "FAN onAdClicked")
                    }

                    override fun onLoggingImpression(ad: Ad?) {
                        logger.d(TAG, "FAN onLoggingImpression")
                    }

                    override fun onRewardedInterstitialCompleted() {
                        logger.d(TAG, "FAN onRewardedInterstitialCompleted")
                    }

                    override fun onRewardedInterstitialClosed() {
                        logger.d(TAG, "FAN onRewardedInterstitialClosed")
                        adStateManager.onAdDismissed()
                        fanRewardedInterstitialAd = null
                        loadAd()
                    }
                }).build()
            )
        }
    }

    fun showAd(activity: Activity) {
        if (adStateManager.isPremium) {
            logger.d(TAG, "showAd: Skipped because user is premium")
            return
        }
        if (adStateManager.isAnyAdShowing) {
            logger.d(TAG, "showAd: Skipped because another full-screen ad is currently showing")
            return
        }

        if (admobInterstitialAd != null) {
            logger.d(TAG, "showAd: Displaying Admob Interstitial")
            adStateManager.onAdShown()
            admobInterstitialAd?.adEventCallback = object : InterstitialAdEventCallback {
                override fun onAdDismissedFullScreenContent() {
                    logger.d(TAG, "onAdDismissedFullScreenContent: Admob Interstitial dismissed")
                    admobInterstitialAd = null
                    adStateManager.onAdDismissed()
                    loadAd()
                }

                override fun onAdFailedToShowFullScreenContent(fullScreenContentError: FullScreenContentError) {
                    logger.e(
                        TAG,
                        "onAdFailedToShowFullScreenContent: Admob Interstitial failed to show: ${fullScreenContentError.message}"
                    )
                    admobInterstitialAd = null
                    adStateManager.onAdDismissed()
                    loadAd()
                }
            }
            admobInterstitialAd?.show(activity)
            admobInterstitialAd = null
        } else if (fanRewardedInterstitialAd?.isAdLoaded == true) {
            logger.d(TAG, "showAd: Displaying FAN Rewarded Interstitial")
            adStateManager.onAdShown()
            fanRewardedInterstitialAd?.show()
        } else {
            logger.w(TAG, "showAd: No ad loaded, fallback to InAppReview")
            // TODO: InAppReview.show(activity) once InAppReview utility is migrated.
        }
    }
}
