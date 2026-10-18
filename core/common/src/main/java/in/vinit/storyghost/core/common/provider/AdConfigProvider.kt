package `in`.vinit.storyghost.core.common.provider

interface AdConfigProvider {
    fun getAdmobAppId(): String
    fun getAdmobAppOpenAdId(): String
    fun getAdmobInterstitialAdId(): String
    fun getInterstitialFirstPriorityNetwork(): String
}
