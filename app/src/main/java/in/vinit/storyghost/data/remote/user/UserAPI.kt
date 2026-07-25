package `in`.vinit.storyghost.data.remote.user

import `in`.vinit.storyghost.data.model.SearchUserResponse
import `in`.vinit.storyghost.data.model.UserInfoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UserAPI {

    @GET("graphql/query/")
    suspend fun getUserInfo(
        @Query("query_hash") queryHash: String,
        @Query("variables") variables: String
    ): UserInfoResponse

    @GET("web/search/topsearch/")
    suspend fun searchUser(@Query("query") query: String): SearchUserResponse
}