package `in`.vinit.storyghost.core.model

data class SubscriptionPlan(
    val productId: String,
    val formattedPrice: String,
    val effectiveMonthlyPrice: String? = null
)
