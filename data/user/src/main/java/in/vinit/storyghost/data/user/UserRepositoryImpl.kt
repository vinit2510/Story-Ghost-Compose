package `in`.vinit.storyghost.data.user

import com.google.gson.Gson
import `in`.vinit.storyghost.config.AppConfig
import `in`.vinit.storyghost.data.model.SearchUserResponse
import `in`.vinit.storyghost.data.model.UserInfoResponse
import `in`.vinit.storyghost.data.remote.user.UserAPI

/**
 * Concrete implementation of [UserRepository] interacting with [in.vinit.storyghost.data.remote.user.UserAPI].
 */
class UserRepositoryImpl(
    private val api: UserAPI,
    private val appConfig: AppConfig
) : UserRepository {

    override suspend fun getUserInfoByUserId(userId: String): UserInfoResponse {
        val queryHash = appConfig.qhUserInfo
        val vars = mapOf(
            "user_id" to userId,
            "include_chaining" to false,
            "include_reel" to true,
            "include_suggested_users" to false,
            "include_logged_out_extras" to false,
            "include_highlight_reels" to false,
            "include_related_profiles" to false
        )
        return api.getUserInfoByUserId(queryHash, Gson().toJson(vars))
    }

    override suspend fun searchUsersByQuery(query: String): SearchUserResponse {
        return api.searchUsersByQuery(query)
    }
}
