package `in`.vinit.storyghost.data.model

import androidx.compose.runtime.Immutable
import com.google.gson.annotations.SerializedName

@Immutable
data class StoryListResponse(
    @SerializedName("data") val data: Data? = null
) {
    @Immutable
    data class Data(
        @SerializedName("user") val user: User? = null
    ) {
        @Immutable
        data class User(
            @SerializedName("id") val id: String? = null,
            @SerializedName("profile_pic_url") val profilePicUrl: String? = null,
            @SerializedName("username") val username: String? = null,
            @SerializedName("feed_reels_tray") val feedReelsTray: FeedReelsTray? = null
        ) {
            @Immutable
            data class FeedReelsTray(
                @SerializedName("edge_reels_tray_to_reel") val edgeReelsTrayToReel: EdgeReelsTrayToReel? = null
            ) {
                @Immutable
                data class EdgeReelsTrayToReel(
                    @SerializedName("edges") val edges: List<Edge>? = null
                ) {
                    @Immutable
                    data class Edge(
                        @SerializedName("node") val node: Node? = null
                    ) {
                        @Immutable
                        data class Node(
                            @SerializedName("id") val id: String? = null,
                            @SerializedName("user") val user: ReelUser? = null
                        ) {
                            @Immutable
                            data class ReelUser(
                                @SerializedName("id") val id: String? = null,
                                @SerializedName("profile_pic_url") val profilePicUrl: String? = null,
                                @SerializedName("username") val username: String? = null
                            )
                        }
                    }
                }
            }
        }
    }
}
