package `in`.vinit.storyghost.config

data class AppConfig(
    val versionCode: Int,
    val isDebug: Boolean,
    val applicationId: String,
    val baseUrl: String,
    val iBaseUrl: String,
    val isMock: Boolean,
    val onesignalAppId: String,
    val docIdStoryAndHighlight: String,
    val qhHighlightList: String,
    val qhUserInfo: String,
    val fanRewardedInterstitialAdId: String,
    val admobRewardedInterstitialAdId: String,
    val admobInterstitialAdId: String,
    val admobAppOpenAdId: String,
    val admobAppId: String,
)
