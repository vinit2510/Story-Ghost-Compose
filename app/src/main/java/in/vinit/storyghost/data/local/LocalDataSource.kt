package `in`.vinit.storyghost.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalDataSource(private val dataStore: DataStore<Preferences>) {

    object Keys {
        val IS_ONBOARDED = booleanPreferencesKey("IS_ONBOARDED")
        val IS_LOGGED = booleanPreferencesKey("IS_LOGGED")
        val COOKIE = stringPreferencesKey("COOKIE")
        val SESSION_ID = stringPreferencesKey("SESSIONID")
        val CSRF_TOKEN = stringPreferencesKey("CSRFTOKEN")
        val USER_ID = stringPreferencesKey("DS_USER_ID")
        val USER_DATA = stringPreferencesKey("USER_DATA")
        val IS_PREMIUM = booleanPreferencesKey("IS_PREMIUM")
        val PLAN_ID = stringPreferencesKey("PLAN_ID")
        val INSTALL_TIME = longPreferencesKey("INSTALL_TIME")
        val REVIEW_TRIES = intPreferencesKey("REVIEW_TRIES")
    }

    suspend fun setOnboarded(value: Boolean) = save(Keys.IS_ONBOARDED, value)
    fun isOnboarded(): Flow<Boolean> = get(Keys.IS_ONBOARDED, false)

    suspend fun setLoggedIn(value: Boolean) = save(Keys.IS_LOGGED, value)
    fun isLoggedIn(): Flow<Boolean> = get(Keys.IS_LOGGED, false)

    suspend fun saveCookie(value: String) = save(Keys.COOKIE, value)
    fun getCookie(): Flow<String?> = getNullable(Keys.COOKIE)

    suspend fun saveSessionId(value: String) = save(Keys.SESSION_ID, value)
    fun getSessionId(): Flow<String?> = getNullable(Keys.SESSION_ID)

    suspend fun saveCsrfToken(value: String) = save(Keys.CSRF_TOKEN, value)
    fun getCsrfToken(): Flow<String?> = getNullable(Keys.CSRF_TOKEN)

    suspend fun saveUserId(value: String) = save(Keys.USER_ID, value)
    fun getUserId(): Flow<String?> = getNullable(Keys.USER_ID)

    suspend fun saveUserData(value: String) = save(Keys.USER_DATA, value)
    fun getUserData(): Flow<String?> = getNullable(Keys.USER_DATA)

    suspend fun setPremium(value: Boolean) = save(Keys.IS_PREMIUM, value)
    fun isPremium(): Flow<Boolean> = get(Keys.IS_PREMIUM, false)

    suspend fun savePlanId(value: String?) {
        dataStore.edit {
            if (value != null) it[Keys.PLAN_ID] = value
            else it.remove(Keys.PLAN_ID)
        }
    }

    fun getPlanId(): Flow<String?> = getNullable(Keys.PLAN_ID)

    suspend fun saveInstallTime(value: Long) = save(Keys.INSTALL_TIME, value)
    fun getInstallTime(): Flow<Long> = get(Keys.INSTALL_TIME, 0L)

    suspend fun saveReviewTries(value: Int) = save(Keys.REVIEW_TRIES, value)
    fun getReviewTries(): Flow<Int> = get(Keys.REVIEW_TRIES, 0)

    suspend fun clearAuthentication() {
        dataStore.edit {
            it.remove(Keys.IS_LOGGED)
            it.remove(Keys.COOKIE)
            it.remove(Keys.SESSION_ID)
            it.remove(Keys.CSRF_TOKEN)
            it.remove(Keys.USER_ID)
            it.remove(Keys.USER_DATA)
        }
    }

    private suspend fun <T> save(key: Preferences.Key<T>, value: T) {
        dataStore.edit { it[key] = value }
    }

    private fun <T> get(key: Preferences.Key<T>, default: T): Flow<T> {
        return dataStore.data.map { it[key] ?: default }
    }

    private fun getNullable(key: Preferences.Key<String>): Flow<String?> =
        dataStore.data.map { it[key] }

    suspend fun clearAll() = dataStore.edit { it.clear() }

}