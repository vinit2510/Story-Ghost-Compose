package `in`.vinit.storyghost.data.user

import `in`.vinit.storyghost.data.model.SearchUserResponse
import `in`.vinit.storyghost.data.model.UserInfoResponse
import `in`.vinit.storyghost.data.remote.user.UserAPI

/**
 * Concrete implementation of [UserRepository] interacting with [in.vinit.storyghost.data.remote.user.UserAPI].
 */
class UserRepositoryImpl(
    private val api: UserAPI
) : UserRepository {

    override suspend fun getUserInfoByUserId(userId: String): UserInfoResponse {
        return api.getUserInfoByUserId(userId = userId)
    }

    override suspend fun searchUsersByQuery(query: String): SearchUserResponse {
        return api.searchUsersByQuery(
            query = query,
            context = "blended",
            searchSurface = "top_search_page"
        )
    }
}
