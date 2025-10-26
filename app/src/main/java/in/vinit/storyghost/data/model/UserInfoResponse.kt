package `in`.vinit.storyghost.data.model

import com.google.gson.annotations.SerializedName

data class UserInfoResponse(
    @SerializedName("user") val user: User? = null
) {
    data class User(
        @SerializedName("id") val id: String? = "",
        @SerializedName("full_name") val fullName: String? = "",
        @SerializedName("profile_pic_url") val profilePicUrl: String? = "",
        @SerializedName("username") val username: String? = "",
    )
}