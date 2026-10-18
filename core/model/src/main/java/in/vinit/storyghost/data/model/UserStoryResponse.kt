package `in`.vinit.storyghost.data.model

import androidx.compose.runtime.Immutable
import com.google.gson.annotations.SerializedName

@Immutable
data class UserStoryResponse(
    @SerializedName("data") val data: Data? = null
) {

    @Immutable
    data class Data(
        @SerializedName("reels_media") val reelsMedia: List<ReelsMedia>? = null
    ) {
        @Immutable
        data class ReelsMedia(
            @SerializedName("items") val items: List<Item>? = null
        ) {
            @Immutable
            data class Item(
                @SerializedName("__typename") val typename: String? = null,
                @SerializedName("id") val id: String? = null,
                @SerializedName("taken_at_timestamp") val takenAtTimeStamp: Long? = null,
                @SerializedName("owner") val owner: Owner? = null,
                @SerializedName("display_url") val displayUrl: String? = null,
                @SerializedName("video_resources") val videoResources: List<VideoResource>? = null,
                @SerializedName("tappable_objects") val tappableObjects: List<TappableObject>? = null
            ) {
                @Immutable
                data class Owner(
                    @SerializedName("id") val id: String? = null,
                    @SerializedName("profile_pic_url") val profilePicUrl: String? = null,
                    @SerializedName("username") val username: String? = null,
                )

                @Immutable
                data class VideoResource(
                    @SerializedName("src") val src: String? = null
                )

                @Immutable
                data class TappableObject(
                    @SerializedName("__typename") val typename: String? = null,
                    @SerializedName("x") val x: Double = 0.0,
                    @SerializedName("y") val y: Double = 0.0,
                    @SerializedName("username") val username: String? = null,
                    @SerializedName("full_name") val fullName: String? = null
                )
            }
        }
    }
}
