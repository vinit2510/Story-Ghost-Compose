package `in`.vinit.storyghost.core.ads

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
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.core.play.InAppReviewManager
import `in`.vinit.storyghost.data.remote.config.RemoteConfigRepository
import com.facebook.ads.AdError as FANAdError
import com.facebook.ads.RewardedInterstitialAd as FANRewardedInterstitialAd

private const val TAG = "InterstitialAdManager"

class InterstitialAdManager(
    private val context: Context,
    private val logger: Logger,
    private val adStateManager: AdStateManager,
    private val inAppReviewManager: InAppReviewManager,
    private val remoteConfigRepository: RemoteConfigRepository
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
        val priority =
            remoteConfigRepository.getInterstitialFirstPriorityNetwork().trim().lowercase()
        if (priority == "facebook") {
            loadFanAd(fallbackToAdmob = true)
        } else {
            loadAdmobAd(fallbackToFan = true)
        }
    }

    private fun loadAdmobAd(fallbackToFan: Boolean) {
        logger.d(
            TAG,
            "loadAdmobAd: Starting to load Admob Interstitial (fallbackToFan=$fallbackToFan)"
        )
        isAdLoading = true

        val admobInterstitialAdId = remoteConfigRepository.getAdmobInterstitialAdId()
        val adRequest = AdRequest.Builder(admobInterstitialAdId).build()

        InterstitialAd.load(
            adRequest,
            object : AdLoadCallback<InterstitialAd> {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    logger.e(TAG, "Admob onAdFailedToLoad: ${adError.message}")
                    isAdLoading = false
                    admobInterstitialAd = null
                    if (fallbackToFan) {
                        loadFanAd(fallbackToAdmob = false)
                    } else {
                        adStateManager.onAdDismissed()
                    }
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    logger.d(TAG, "Admob onAdLoaded")
                    isAdLoading = false
                    admobInterstitialAd = ad
                }
            }
        )
    }

    private fun loadFanAd(fallbackToAdmob: Boolean) {
        logger.d(
            TAG,
            "loadFanAd: Starting to load FAN Rewarded Interstitial (fallbackToAdmob=$fallbackToAdmob)"
        )
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
                        if (fallbackToAdmob) {
                            loadAdmobAd(fallbackToFan = false)
                        } else {
                            adStateManager.onAdDismissed()
                        }
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
        if (activity.isFinishing || activity.isDestroyed) {
            logger.w(TAG, "showAd: Skipped because activity is finishing or destroyed")
            return
        }
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
            inAppReviewManager.show(activity)
        }
    }
}
