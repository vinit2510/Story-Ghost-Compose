package `in`.vinit.storyghost.core.common.storage

import kotlinx.coroutines.flow.Flow

/**
 * Storage contract for persistent application settings, credentials, and user state.
 *
 * Implemented using Jetpack DataStore Preferences to provide asynchronous,
 * thread-safe, and reactive state observation through Kotlin [Flow].
 */
interface LocalStorage {

    /** Returns whether the user has completed the onboarding flow. */
    fun isOnboarded(): Flow<Boolean>

    /** Updates the onboarding completion flag. */
    suspend fun setOnboarded(value: Boolean)

    /** Returns whether an active user session is authenticated. */
    fun isLoggedIn(): Flow<Boolean>

    /** Updates the user login status flag. */
    suspend fun setLoggedIn(value: Boolean)

    /** Returns the full raw session cookie header, or null if unauthenticated. */
    fun getCookie(): Flow<String?>

    /** Persists the raw session cookie header. */
    suspend fun saveCookie(value: String)

    /** Returns the parsed Instagram session ID (`sessionid`). */
    fun getSessionId(): Flow<String?>

    /** Persists the parsed Instagram session ID. */
    suspend fun saveSessionId(value: String)

    /** Returns the parsed CSRF token (`csrftoken`). */
    fun getCsrfToken(): Flow<String?>

    /** Persists the parsed CSRF token. */
    suspend fun saveCsrfToken(value: String)

    /** Returns the authenticated Instagram user ID (`ds_user_id`). */
    fun getUserId(): Flow<String?>

    /** Persists the authenticated Instagram user ID. */
    suspend fun saveUserId(value: String)

    /** Returns the cached JSON payload of the logged-in user profile. */
    fun getUserData(): Flow<String?>

    /** Persists the JSON payload of the logged-in user profile. */
    suspend fun saveUserData(value: String)

    /** Returns whether the user possesses an active ad-free premium entitlement. */
    fun isPremium(): Flow<Boolean>

    /** Updates the ad-free premium entitlement status. */
    suspend fun setPremium(value: Boolean)

    /** Returns the subscribed Google Play billing product ID / plan ID. */
    fun getPlanId(): Flow<String?>

    /** Persists the active Google Play billing product ID / plan ID. */
    suspend fun savePlanId(value: String?)

    /** Returns the epoch timestamp of the initial app installation. */
    fun getInstallTime(): Flow<Long>

    /** Persists the initial installation timestamp. */
    suspend fun saveInstallTime(value: Long)

    /** Returns the number of times in-app review was prompted. */
    fun getReviewTries(): Flow<Int>

    /** Updates the number of in-app review prompts. */
    suspend fun saveReviewTries(value: Int)

    /**
     * Clears authentication credentials (cookies, session ID, CSRF token, user ID, user data)
     * and resets login status while preserving app configuration (install time, premium status).
     */
    suspend fun clearAuthentication()

    /**
     * Completely wipes all persistent preferences stored on device.
     */
    suspend fun clearAll()
}
