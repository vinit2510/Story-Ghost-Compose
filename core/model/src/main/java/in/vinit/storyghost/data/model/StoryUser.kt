package `in`.vinit.storyghost.data.model

import androidx.compose.runtime.Immutable

/**
 * UI and domain model representing a user with active stories in the home feed or search results.
 */
@Immutable
data class StoryUser(
    val id: String? = null,
    val username: String? = null,
    val profilePicUrl: String? = null
)
