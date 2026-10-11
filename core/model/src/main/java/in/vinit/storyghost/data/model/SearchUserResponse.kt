package `in`.vinit.storyghost.data.model

import com.google.gson.annotations.SerializedName

data class SearchUserResponse(
    @SerializedName("users") val users: ArrayList<Data>?
) {
    data class Data(
        @SerializedName("user") val user: User? = null
    ) {
        data class User(
            @SerializedName("id") val id: String?,
            @SerializedName("username") val username: String?,
            @SerializedName("full_name") val fullName: String?,
            @SerializedName("is_private") val isPrivate: Boolean?,
            @SerializedName("profile_pic_url") val profilePicUrl: String?,
            @SerializedName("latest_reel_media") val latestReelMedia: Long?,
        )
    }
}