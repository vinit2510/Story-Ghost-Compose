package `in`.vinit.storyghost.data.model

import androidx.compose.runtime.Immutable
import com.google.gson.annotations.SerializedName

/**
 * Direct mapping of the Instagram User Info API response (`api/v1/users/{userId}/info/`).
 */
@Immutable
data class UserInfoResponse(
    @SerializedName("user") val user: User? = null,
    @SerializedName("status") val status: String? = null
) {
    @Immutable
    data class User(
        @SerializedName("pk") val pk: Long? = null,
        @SerializedName("id") val id: String? = null,
        @SerializedName("username") val username: String? = null,
        @SerializedName("full_name") val fullName: String? = null,
        @SerializedName("profile_pic_url") val profilePicUrl: String? = null,
        @SerializedName("profile_pic_id") val profilePicId: String? = null,
        @SerializedName("is_private") val isPrivate: Boolean? = null,
        @SerializedName("is_verified") val isVerified: Boolean? = null
    )
}
