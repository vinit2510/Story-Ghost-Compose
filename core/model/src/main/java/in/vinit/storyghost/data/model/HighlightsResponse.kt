package `in`.vinit.storyghost.data.model

import androidx.compose.runtime.Immutable
import com.google.gson.annotations.SerializedName

@Immutable
data class HighlightsResponse(
    @SerializedName("data") val data: Data? = null
) {
    @Immutable
    data class Data(
        @SerializedName("user") val user: User? = null
    ) {
        @Immutable
        data class User(
            @SerializedName("edge_highlight_reels") val edgeHighlightReels: EdgeHighlightReels? = null
        ) {
            @Immutable
            data class EdgeHighlightReels(
                @SerializedName("edges") val edges: List<Edge>? = null
            ) {
                @Immutable
                data class Edge(
                    @SerializedName("node") val node: Node? = null
                ) {
                    @Immutable
                    data class Node(
                        @SerializedName("id") val id: String? = null,
                        @SerializedName("title") val title: String? = null,
                        @SerializedName("cover_media") val coverMedia: CoverMedia? = null
                    ) {
                        @Immutable
                        data class CoverMedia(
                            @SerializedName("thumbnail_src") val thumbnailSrc: String? = null,
                        )
                    }
                }
            }
        }
    }
}