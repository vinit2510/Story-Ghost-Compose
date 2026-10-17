package `in`.vinit.storyghost.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.res.stringResource

/**
 * Abstraction for text in ViewModels that can either be a raw string or an Android string resource ID.
 *
 * Allows ViewModels to prepare localized error/info messages without holding a reference to [Context],
 * and resolve them later either in a Composable via [asString()] or with an Android [Context] via [asString(context)].
 */
@Immutable
sealed class UiText {
    /** A raw runtime string. */
    @Immutable
    data class DynamicString(val value: String) : UiText()

    /** A localized Android string resource with optional format arguments. */
    @Immutable
    data class StringResource(
        @param:StringRes val resId: Int,
        val args: List<Any> = emptyList()
    ) : UiText()

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(
                resId, *args.toTypedArray()
            )
        }
    }

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(
                resId, *args.toTypedArray()
            )
        }
    }
}