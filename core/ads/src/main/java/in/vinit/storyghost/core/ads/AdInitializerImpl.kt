package `in`.vinit.storyghost.core.ads

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.facebook.ads.AudienceNetworkAds
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import `in`.vinit.storyghost.config.AppConfig
import `in`.vinit.storyghost.core.common.AdInitializer
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.core.common.provider.AdConfigProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdInitializerImpl(
    private val context: Context,
    private val appConfig: AppConfig,
    private val adConfigProvider: AdConfigProvider,
    private val logger: Logger
) : AdInitializer {

    private val scope = CoroutineScope(Dispatchers.IO)

    @RequiresPermission(Manifest.permission.INTERNET)
    override fun initialize() {
        try {
            // Initialize Facebook Audience Network
            AudienceNetworkAds.initialize(context)

            // Initialize Next-Gen AdMob SDK on a background thread
            scope.launch {
                try {
                    val remoteAdmobAppId = adConfigProvider.getAdmobAppId()
                    val admobAppId = remoteAdmobAppId.ifBlank { appConfig.admobAppId }
                    MobileAds.initialize(
                        context,
                        InitializationConfig.Builder(admobAppId).build()
                    ) {}
                } catch (e: Exception) {
                    logger.e("AdInitializerImpl", "Failed to initialize MobileAds", e)
                }
            }
        } catch (e: Exception) {
            logger.e("AdInitializerImpl", "Failed to initialize AudienceNetworkAds", e)
        }
    }
}
