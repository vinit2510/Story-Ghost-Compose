package `in`.vinit.storyghost.data.model

import com.google.gson.annotations.SerializedName

data class UserInfoResponse(
    @SerializedName("data") val data: Data? = null
) {
    data class Data(
        @SerializedName("user") val userContainer: UserContainer? = null
    ) {
        data class UserContainer(
            @SerializedName("reel") val reel: Reel? = null
        ) {
            data class Reel(
                @SerializedName("user") val user: User? = null
            ) {
                data class User(
                    @SerializedName("id") val id: String? = "",
                    @SerializedName("full_name") val fullName: String? = "",
                    @SerializedName("profile_pic_url") val profilePicUrl: String? = "",
                    @SerializedName("username") val username: String? = ""
                )
            }
        }
    }
}