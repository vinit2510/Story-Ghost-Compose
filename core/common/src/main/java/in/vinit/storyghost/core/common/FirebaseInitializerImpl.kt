package `in`.vinit.storyghost.core.common

import android.content.Context
import com.google.firebase.FirebaseApp

class FirebaseInitializerImpl(
    private val context: Context,
    private val logger: Logger
) : FirebaseInitializer {

    override fun initialize() {
        try {
            FirebaseApp.initializeApp(context)
        } catch (e: Exception) {
            logger.e("FirebaseInitializerImpl", "Failed to initialize FirebaseApp", e)
        }
    }
}
