package `in`.vinit.storyghost.data.model

import androidx.compose.runtime.Immutable
import com.google.gson.annotations.SerializedName

@Immutable
data class UserStoryResponse(
    @SerializedName("data") val data: Data? = null
) {
    @Immutable
    data class Data(
        @SerializedName("xdt_api__v1__feed__reels_media") val xdtReelsMedia: XdtReelsMedia? = null
    ) {
        @Immutable
        data class XdtReelsMedia(
            @SerializedName("reels_media") val reelsMedia: List<ReelsMedia>? = null
        )

        @Immutable
        data class ReelsMedia(
            @SerializedName("id") val id: String? = null,
            @SerializedName("user") val user: User? = null,
            @SerializedName("items") val items: List<Item>? = null
        ) {
            @Immutable
            data class Item(
                @SerializedName("id") val id: String? = null,
                @SerializedName("pk") val pk: String? = null,
                @SerializedName("media_type") val mediaType: Int? = null,
                @SerializedName("taken_at") val takenAt: Long? = null,
                @SerializedName("user") val user: User? = null,
                @SerializedName("image_versions2") val imageVersions2: ImageVersions? = null,
                @SerializedName("video_versions") val videoVersions: List<VideoVersion>? = null,
                @SerializedName("story_bloks_stickers") val storyBloksStickers: List<StoryBloksSticker>? = null
            )
        }

        @Immutable
        data class User(
            @SerializedName("id") val id: String? = null,
            @SerializedName("pk") val pk: String? = null,
            @SerializedName("username") val username: String? = null,
            @SerializedName("profile_pic_url") val profilePicUrl: String? = null
        )

        @Immutable
        data class ImageVersions(
            @SerializedName("candidates") val candidates: List<Candidate>? = null
        ) {
            @Immutable
            data class Candidate(
                @SerializedName("url") val url: String? = null,
                @SerializedName("width") val width: Int? = null,
                @SerializedName("height") val height: Int? = null
            )
        }

        @Immutable
        data class VideoVersion(
            @SerializedName("url") val url: String? = null,
            @SerializedName("type") val type: Int? = null
        )

        @Immutable
        data class StoryBloksSticker(
            @SerializedName("x") val x: Double = 0.0,
            @SerializedName("y") val y: Double = 0.0,
            @SerializedName("width") val width: Double = 0.0,
            @SerializedName("height") val height: Double = 0.0,
            @SerializedName("bloks_sticker") val bloksSticker: BloksSticker? = null
        ) {
            @Immutable
            data class BloksSticker(
                @SerializedName("sticker_data") val stickerData: StickerData? = null
            ) {
                @Immutable
                data class StickerData(
                    @SerializedName("ig_mention") val igMention: IgMention? = null
                ) {
                    @Immutable
                    data class IgMention(
                        @SerializedName("username") val username: String? = null,
                        @SerializedName("full_name") val fullName: String? = null
                    )
                }
            }
        }
    }
}
