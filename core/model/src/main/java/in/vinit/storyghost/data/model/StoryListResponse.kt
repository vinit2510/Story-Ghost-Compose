package `in`.vinit.storyghost.data.model

import com.google.gson.annotations.SerializedName


data class StoryListResponse(
    @SerializedName("data") val data: Data? = null
) {
    data class Data(
        @SerializedName("user") val user: User? = null
    ) {
        data class User(
            @SerializedName("id") val id: String? = null,
            @SerializedName("profile_pic_url") val profilePicUrl: String? = null,
            @SerializedName("username") val username: String? = null,
            @SerializedName("feed_reels_tray") val feedReelsTray: FeedReelsTray? = null
        ) {
            data class FeedReelsTray(
                @SerializedName("edge_reels_tray_to_reel") val edgeReelsTrayToReel: EdgeReelsTrayToReel? = null
            ) {
                data class EdgeReelsTrayToReel(
                    @SerializedName("edges") val edges: ArrayList<Edge>? = null
                ) {
                    data class Edge(
                        @SerializedName("node") val node: Node? = null
                    ) {
                        data class Node(
                            @SerializedName("id") val id: String? = null,
                            @SerializedName("user") val user: ReelUser? = null
                        ) {
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
