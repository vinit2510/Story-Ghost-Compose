package `in`.vinit.storyghost.data.remote.user

import `in`.vinit.storyghost.data.model.SearchUserResponse
import `in`.vinit.storyghost.data.model.UserInfoResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface defining endpoints for Instagram user profile data and user search.
 */
interface UserAPI {

    /**
     * Fetches user profile information for a given user ID.
     *
     * @param queryHash The GraphQL query hash for user profile info query.
     * @param variables Serialized JSON string containing target user ID.
     * @return [UserInfoResponse] containing profile details.
     */
    @GET("graphql/query/")
    suspend fun getUserInfoByUserId(
        @Query("query_hash") queryHash: String,
        @Query("variables") variables: String
    ): UserInfoResponse

    /**
     * Searches users matching a query string via Instagram top search endpoint.
     *
     * @param query The search query string (username or name).
     * @param context The search context, defaults to "blended".
     * @param searchSurface The search surface, defaults to "top_search_page".
     * @return [SearchUserResponse] containing matching user search results.
     */
    @GET("api/v1/fbsearch/topsearch_flat/")
    suspend fun searchUsersByQuery(
        @Query("query") query: String,
        @Query("context") context: String = "blended",
        @Query("search_surface") searchSurface: String = "top_search_page"
    ): SearchUserResponse
}