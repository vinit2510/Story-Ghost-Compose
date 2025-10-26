package `in`.vinit.storyghost.data.remote.user

import `in`.vinit.storyghost.data.model.SearchUserResponse
import `in`.vinit.storyghost.data.model.UserInfoResponse
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.util.makeApiCall

interface UserRepository {
    suspend fun getUserInfo(userId: String): UiState<UserInfoResponse>
    suspend fun searchUser(query: String): UiState<SearchUserResponse>
}

class UserRepositoryImpl(private val api: UserAPI) : UserRepository {
    override suspend fun getUserInfo(userId: String) = makeApiCall { api.getUserInfo(userId) }
    override suspend fun searchUser(query: String) = makeApiCall { api.searchUser(query) }
}
