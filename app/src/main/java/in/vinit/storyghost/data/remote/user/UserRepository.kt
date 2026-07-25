package `in`.vinit.storyghost.data.remote.user

import com.google.gson.Gson
import `in`.vinit.storyghost.BuildConfig
import `in`.vinit.storyghost.data.model.SearchUserResponse
import `in`.vinit.storyghost.data.model.UserInfoResponse

interface UserRepository {
    suspend fun getUserInfo(userId: String): UserInfoResponse
    suspend fun searchUser(query: String): SearchUserResponse
}

class UserRepositoryImpl(private val api: UserAPI) : UserRepository {

    override suspend fun getUserInfo(userId: String): UserInfoResponse {
        val queryHash = BuildConfig.QH_USER_INFO
        val vars = mapOf(
            "user_id" to userId,
            "include_chaining" to false,
            "include_reel" to true,
            "include_suggested_users" to false,
            "include_logged_out_extras" to false,
            "include_highlight_reels" to false,
            "include_related_profiles" to false
        )
        return api.getUserInfo(queryHash, Gson().toJson(vars))
    }

    override suspend fun searchUser(query: String): SearchUserResponse {
        return api.searchUser(query)
    }
}
