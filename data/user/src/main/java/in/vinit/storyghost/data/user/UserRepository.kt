package `in`.vinit.storyghost.data.user

import `in`.vinit.storyghost.data.model.SearchUserResponse
import `in`.vinit.storyghost.data.model.UserInfoResponse

/**
 * Repository interface providing user profile details and search operations.
 */
interface UserRepository {

    /**
     * Fetches profile details for a given user ID.
     *
     * @param userId Target user ID.
     * @return [UserInfoResponse] containing profile information.
     */
    suspend fun getUserInfoByUserId(userId: String): UserInfoResponse

    /**
     * Searches users matching a query string.
     *
     * @param query Search query string.
     * @return [SearchUserResponse] containing matching users.
     */
    suspend fun searchUsersByQuery(query: String): SearchUserResponse
}
