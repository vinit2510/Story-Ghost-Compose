package `in`.vinit.storyghost.data.remote.user

import `in`.vinit.storyghost.data.model.SearchUserResponse
import `in`.vinit.storyghost.data.model.UserInfoResponse

interface UserRepository {
    suspend fun getUserInfo(userId: String): UserInfoResponse
    suspend fun searchUser(query: String): SearchUserResponse
}

class UserRepositoryImpl(private val api: UserAPI) : UserRepository {

    override suspend fun getUserInfo(userId: String): UserInfoResponse {
        return api.getUserInfo(userId)
    }

    override suspend fun searchUser(query: String): SearchUserResponse {
        return api.searchUser(query)
    }
}
