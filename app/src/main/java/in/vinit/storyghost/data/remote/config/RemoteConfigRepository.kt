package `in`.vinit.storyghost.data.remote.config

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import `in`.vinit.storyghost.R


private const val TAG = "RemoteConfigRepository"

private const val INSTAGRAM_LOGIN_URL = "instagram_login_url"
private const val INSTAGRAM_ACCOUNT_URL = "instagram_account_url"
private const val FORCE_UPDATE_VERSION = "force_update_version"
private const val USER_AGENT = "user_agent"
private const val COOKIE_MIN_VERSION = "cookie_min_version"

interface RemoteConfigRepository {
    fun getInstagramLoginUrl(): String
    fun getInstagramAccountUrl(): String
    fun getForceUpdateVersion(): Int
    fun getUserAgent(): String
    fun getCookieMinVersion(): Int
}

class FirebaseRemoteConfigImpl : RemoteConfigRepository {

    private val firebaseRemoteConfig = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        try {
            firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "fetchAndActivate: Success")
                } else {
                    Log.d(TAG, "fetchAndActivate: Failure")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "fetchAndActivate: ", e)
        }
    }

    override fun getInstagramLoginUrl() = firebaseRemoteConfig.getString(INSTAGRAM_LOGIN_URL)

    override fun getInstagramAccountUrl() = firebaseRemoteConfig.getString(INSTAGRAM_ACCOUNT_URL)

    override fun getForceUpdateVersion() =
        firebaseRemoteConfig.getLong(FORCE_UPDATE_VERSION).toInt()

    override fun getUserAgent() = firebaseRemoteConfig.getString(USER_AGENT)

    override fun getCookieMinVersion() = firebaseRemoteConfig.getLong(COOKIE_MIN_VERSION).toInt()

}