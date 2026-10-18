package `in`.vinit.storyghost.data.model

import androidx.compose.runtime.Immutable
import com.google.gson.annotations.SerializedName

@Immutable
data class FollowingResponse(
    @SerializedName("users") val users: List<User>? = null,
    @SerializedName("big_list") val bigList: Boolean? = null,
    @SerializedName("page_size") val pageSize: Int? = null,
    @SerializedName("next_max_id") val nextMaxId: String? = null,
    @SerializedName("has_more") val hasMore: Boolean? = null,
    @SerializedName("status") val status: String? = null
) {
    @Immutable
    data class User(
        @SerializedName("pk") val pk: Long? = null,
        @SerializedName("pk_id") val pkId: Long? = null,
        @SerializedName("id") val id: String? = null,
        @SerializedName("username") val username: String? = null,
        @SerializedName("full_name") val fullName: String? = null,
        @SerializedName("profile_pic_url") val profilePicUrl: String? = null,
        @SerializedName("latest_reel_media") val latestReelMedia: Long? = null,
        @SerializedName("is_private") val isPrivate: Boolean? = null,
        @SerializedName("is_verified") val isVerified: Boolean? = null
    )
}
