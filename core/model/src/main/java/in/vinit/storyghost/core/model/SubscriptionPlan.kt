package `in`.vinit.storyghost.core.model

import androidx.compose.runtime.Immutable

/**
 * Domain model representing an in-app subscription tier offered via Google Play Billing.
 * Annotated with [Immutable] for Compose compiler recomposition skipping.
 */
@Immutable
data class SubscriptionPlan(
    val productId: String,
    val formattedPrice: String,
    val effectiveMonthlyPrice: String? = null
)
