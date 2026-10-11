package `in`.vinit.storyghost.data.model

import com.google.gson.annotations.SerializedName

data class HighlightsResponse(
    @SerializedName("data") val data: Data? = null
) {
    data class Data(
        @SerializedName("user") val user: User? = null
    ) {
        data class User(
            @SerializedName("edge_highlight_reels") val edgeHighlightReels: EdgeHighlightReels? = null
        ) {
            data class EdgeHighlightReels(
                @SerializedName("edges") val edges: ArrayList<Edge>? = null
            ) {
                data class Edge(
                    @SerializedName("node") val node: Node? = null
                ) {
                    data class Node(
                        @SerializedName("id") val id: String? = null,
                        @SerializedName("title") val title: String? = null,
                        @SerializedName("cover_media") val coverMedia: CoverMedia? = null
                    ) {
                        data class CoverMedia(
                            @SerializedName("thumbnail_src") val thumbnailSrc: String? = null,
                        )
                    }
                }
            }
        }
    }
}