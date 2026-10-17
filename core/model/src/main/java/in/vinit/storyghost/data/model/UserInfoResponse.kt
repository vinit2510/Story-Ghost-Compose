package `in`.vinit.storyghost.data.model

import androidx.compose.runtime.Immutable
import com.google.gson.annotations.SerializedName

@Immutable
data class UserInfoResponse(
    @SerializedName("data") val data: Data? = null
) {
    @Immutable
    data class Data(
        @SerializedName("user") val userContainer: UserContainer? = null
    ) {
        @Immutable
        data class UserContainer(
            @SerializedName("reel") val reel: Reel? = null
        ) {
            @Immutable
            data class Reel(
                @SerializedName("user") val user: User? = null
            ) {
                @Immutable
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