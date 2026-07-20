package `in`.vinit.storyghost.data.remote.user

import `in`.vinit.storyghost.BuildConfig
import `in`.vinit.storyghost.data.model.SearchUserResponse
import `in`.vinit.storyghost.data.model.UserInfoResponse

interface UserRepository {
    suspend fun getUserInfo(userId: String): UserInfoResponse
    suspend fun searchUser(query: String): SearchUserResponse
}

class UserRepositoryImpl(private val api: UserAPI) : UserRepository {

    override suspend fun getUserInfo(userId: String): UserInfoResponse {
        val url = BuildConfig.API_USER_INFO.replace("{{userId}}", userId)
        return api.getUserInfo(url)
    }

    override suspend fun searchUser(query: String): SearchUserResponse {
        return api.searchUser(query)
    }
}
