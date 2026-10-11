package `in`.vinit.storyghost.data.billing

import android.app.Activity
import `in`.vinit.storyghost.core.model.SubscriptionPlan
import kotlinx.coroutines.flow.SharedFlow

sealed interface BillingEvent {
    object ServiceDisconnected : BillingEvent
    object PurchaseCanceled : BillingEvent
    object PurchaseSuccess : BillingEvent
    data class PurchaseError(val message: String) : BillingEvent
}

const val MONTHLY_PLAN_ID = "monthly"
const val YEARLY_PLAN_ID = "yearly"

interface BillingRepository {

    /**
     * A hot stream of one-off billing lifecycle events.
     */
    val billingEvents: SharedFlow<BillingEvent>

    /**
     * Queries details from Google Play Billing and returns parsed per-item [SubscriptionPlan] list.
     */
    suspend fun queryProductDetails(): Result<List<SubscriptionPlan>>

    /**
     * Performs an asynchronous query of current active purchases on Google Play.
     */
    fun checkSubscription()

    /**
     * Launches the Google Play purchase overlay flow for the specified [SubscriptionPlan].
     */
    fun purchaseProduct(activity: Activity, plan: SubscriptionPlan)
}
