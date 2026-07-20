package `in`.vinit.storyghost.data.remote.user

import `in`.vinit.storyghost.BuildConfig
import `in`.vinit.storyghost.data.model.SearchUserResponse
import `in`.vinit.storyghost.data.model.UserInfoResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface UserAPI {

    @GET
    suspend fun getUserInfo(@Url url: String): UserInfoResponse

    @GET(BuildConfig.API_SEARCH_USER)
    suspend fun searchUser(@Query("query") query: String): SearchUserResponse
}