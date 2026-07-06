package `in`.vinit.storyghost.data.remote.config

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import `in`.vinit.storyghost.BuildConfig
import `in`.vinit.storyghost.core.common.Logger


private const val TAG = "RemoteConfigRepository"

private const val INSTAGRAM_LOGIN_URL = "instagram_login_url"
private const val INSTAGRAM_ACCOUNT_URL = "instagram_account_url"
private const val FORCE_UPDATE_VERSION = "force_update_version"
private const val USER_AGENT = "user_agent"
private const val COOKIE_MIN_VERSION = "cookie_min_version"
private const val ADMOB_APP_ID = "admob_app_id"
private const val ADMOB_APP_OPEN_AD_ID = "admob_app_open_ad_id"
private const val ADMOB_INTERSTITIAL_AD_ID = "admob_interstitial_ad_id"

interface RemoteConfigRepository {
    fun getInstagramLoginUrl(): String
    fun getInstagramAccountUrl(): String
    fun getForceUpdateVersion(): Int
    fun getUserAgent(): String
    fun getCookieMinVersion(): Int
    fun getAdmobAppId(): String
    fun getAdmobAppOpenAdId(): String
    fun getAdmobInterstitialAdId(): String
}

class FirebaseRemoteConfigImpl(
    private val logger: Logger
) : RemoteConfigRepository {

    private val firebaseRemoteConfig = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        val defaults: Map<String, Any> = mapOf(
            INSTAGRAM_LOGIN_URL to "https://www.instagram.com/accounts/login/",
            INSTAGRAM_ACCOUNT_URL to "https://www.instagram.com/%s/",
            FORCE_UPDATE_VERSION to 1,
            USER_AGENT to "Mozilla/5.0 (iPhone; CPU iPhone OS 12_3_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 Instagram 105.0.0.11.118 (iPhone11,8; iOS 12_3_1; en_US; en-US; scale=2.00; 828x1792; 165586599)",
            COOKIE_MIN_VERSION to 1,
            ADMOB_APP_ID to BuildConfig.ADMOB_APP_ID,
            ADMOB_APP_OPEN_AD_ID to BuildConfig.ADMOB_APP_OPEN_AD_ID,
            ADMOB_INTERSTITIAL_AD_ID to BuildConfig.ADMOB_INTERSTITIAL_AD_ID
        )
        firebaseRemoteConfig.setDefaultsAsync(defaults)

        try {
            firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    logger.d(TAG, "fetchAndActivate: Success")
                } else {
                    logger.e(TAG, "fetchAndActivate: Failure")
                }
            }
        } catch (e: Exception) {
            logger.e(TAG, "fetchAndActivate: ", e)
        }
    }

    override fun getInstagramLoginUrl() = firebaseRemoteConfig.getString(INSTAGRAM_LOGIN_URL)

    override fun getInstagramAccountUrl() = firebaseRemoteConfig.getString(INSTAGRAM_ACCOUNT_URL)

    override fun getForceUpdateVersion() =
        firebaseRemoteConfig.getLong(FORCE_UPDATE_VERSION).toInt()

    override fun getUserAgent() = firebaseRemoteConfig.getString(USER_AGENT)

    override fun getCookieMinVersion() = firebaseRemoteConfig.getLong(COOKIE_MIN_VERSION).toInt()

    override fun getAdmobAppId() = firebaseRemoteConfig.getString(ADMOB_APP_ID)

    override fun getAdmobAppOpenAdId() = firebaseRemoteConfig.getString(ADMOB_APP_OPEN_AD_ID)

    override fun getAdmobInterstitialAdId() =
        firebaseRemoteConfig.getString(ADMOB_INTERSTITIAL_AD_ID)

}