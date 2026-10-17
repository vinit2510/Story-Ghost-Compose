package `in`.vinit.storyghost.data.model

import com.google.gson.annotations.SerializedName

data class SearchUserResponse(
    @SerializedName("list") val list: List<Data>? = null,
    @SerializedName("users") val users: List<Data>? = null,
    @SerializedName("status") val status: String? = null
) {
    val allUsers: List<Data>
        get() = list ?: users ?: emptyList()

    data class Data(
        @SerializedName("position") val position: Int? = null,
        @SerializedName("user") val user: User? = null
    ) {
        data class User(
            @SerializedName("id") val id: String? = null,
            @SerializedName("pk") val pk: String? = null,
            @SerializedName("pk_id") val pkId: String? = null,
            @SerializedName("username") val username: String? = null,
            @SerializedName("full_name") val fullName: String? = null,
            @SerializedName("is_private") val isPrivate: Boolean? = null,
            @SerializedName("profile_pic_url") val profilePicUrl: String? = null,
            @SerializedName("latest_reel_media") val latestReelMedia: Long? = null,
            @SerializedName("is_verified") val isVerified: Boolean? = null
        ) {
            val userId: String?
                get() = id ?: pkId ?: pk
        }
    }
}