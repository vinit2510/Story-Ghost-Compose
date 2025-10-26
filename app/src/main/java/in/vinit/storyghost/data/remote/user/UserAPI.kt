package `in`.vinit.storyghost.data.remote.user

import `in`.vinit.storyghost.BuildConfig
import `in`.vinit.storyghost.data.model.SearchUserResponse
import `in`.vinit.storyghost.data.model.UserInfoResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserAPI {

    @GET(BuildConfig.API_USER_INFO)
    suspend fun getUserInfo(@Path("userId") userId: String): UserInfoResponse

    @GET(BuildConfig.API_SEARCH_USER)
    suspend fun searchUser(@Query("query") query: String): SearchUserResponse
}