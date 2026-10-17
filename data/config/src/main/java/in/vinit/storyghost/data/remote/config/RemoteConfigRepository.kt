package `in`.vinit.storyghost.data.remote.config

interface RemoteConfigRepository {
    fun getInstagramLoginUrl(): String
    fun getInstagramAccountUrl(): String
    fun getForceUpdateVersion(): Int
    fun getUserAgent(): String
    fun getCookieMinVersion(): Int
    fun getAdmobAppId(): String
    fun getAdmobAppOpenAdId(): String
    fun getAdmobInterstitialAdId(): String
    fun getInterstitialFirstPriorityNetwork(): String
    fun getOpenSourceContributionUrl(): String
}
