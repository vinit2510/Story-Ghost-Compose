package `in`.vinit.storyghost.data.model

import androidx.compose.runtime.Immutable
import com.google.gson.annotations.SerializedName

/**
 * Direct mapping of the Instagram Highlights Tray API response (`api/v1/highlights/{userId}/highlights_tray/`).
 */
@Immutable
data class HighlightsResponse(
    @SerializedName("tray") val tray: List<TrayItem>? = null,
    @SerializedName("show_empty_state") val showEmptyState: Boolean? = null,
    @SerializedName("status") val status: String? = null
) {
    @Immutable
    data class TrayItem(
        @SerializedName("id") val id: String? = null,
        @SerializedName("latest_reel_media") val latestReelMedia: Long? = null,
        @SerializedName("seen") val seen: Long? = null,
        @SerializedName("can_reply") val canReply: Boolean? = null,
        @SerializedName("can_reshare") val canReshare: Boolean? = null,
        @SerializedName("reel_type") val reelType: String? = null,
        @SerializedName("cover_media") val coverMedia: CoverMedia? = null,
        @SerializedName("user") val user: User? = null,
        @SerializedName("title") val title: String? = null,
        @SerializedName("created_at") val createdAt: Long? = null,
        @SerializedName("is_pinned_highlight") val isPinnedHighlight: Boolean? = null,
        @SerializedName("prefetch_count") val prefetchCount: Int? = null,
        @SerializedName("media_count") val mediaCount: Int? = null
    ) {
        @Immutable
        data class CoverMedia(
            @SerializedName("media_id") val mediaId: String? = null,
            @SerializedName("crop_rect") val cropRect: List<Float>? = null,
            @SerializedName("cropped_image_version") val croppedImageVersion: ImageVersion? = null,
            @SerializedName("full_image_version") val fullImageVersion: ImageVersion? = null
        ) {
            @Immutable
            data class ImageVersion(
                @SerializedName("width") val width: Int? = null,
                @SerializedName("height") val height: Int? = null,
                @SerializedName("url") val url: String? = null
            )
        }

        @Immutable
        data class User(
            @SerializedName("pk") val pk: Long? = null,
            @SerializedName("id") val id: String? = null,
            @SerializedName("username") val username: String? = null,
            @SerializedName("full_name") val fullName: String? = null,
            @SerializedName("profile_pic_url") val profilePicUrl: String? = null,
            @SerializedName("is_private") val isPrivate: Boolean? = null,
            @SerializedName("is_verified") val isVerified: Boolean? = null
        )
    }
}
