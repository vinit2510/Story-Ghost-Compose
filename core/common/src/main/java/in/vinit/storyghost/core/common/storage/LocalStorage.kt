package `in`.vinit.storyghost.core.common.storage

import kotlinx.coroutines.flow.Flow

interface LocalStorage {
    fun isOnboarded(): Flow<Boolean>
    suspend fun setOnboarded(value: Boolean)

    fun isLoggedIn(): Flow<Boolean>
    suspend fun setLoggedIn(value: Boolean)

    fun getCookie(): Flow<String?>
    suspend fun saveCookie(value: String)

    fun getSessionId(): Flow<String?>
    suspend fun saveSessionId(value: String)

    fun getCsrfToken(): Flow<String?>
    suspend fun saveCsrfToken(value: String)

    fun getUserId(): Flow<String?>
    suspend fun saveUserId(value: String)

    fun getUserData(): Flow<String?>
    suspend fun saveUserData(value: String)

    fun isPremium(): Flow<Boolean>
    suspend fun setPremium(value: Boolean)

    fun getPlanId(): Flow<String?>
    suspend fun savePlanId(value: String?)

    fun getInstallTime(): Flow<Long>
    suspend fun saveInstallTime(value: Long)

    fun getReviewTries(): Flow<Int>
    suspend fun saveReviewTries(value: Int)

    suspend fun clearAuthentication()
    suspend fun clearAll()
}
