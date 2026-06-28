package `in`.vinit.storyghost.data.remote.billing

import android.app.Activity
import com.android.billingclient.api.ProductDetails
import `in`.vinit.storyghost.ui.UiState
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
     * A hot stream of one-off billing lifecycle events (e.g. cancellations, connection disconnections,
     * transaction failures). Declared as a [SharedFlow] with replay = 0 to ensure historical events
     * are not cached or re-emitted to new subscribers upon screen initialization.
     */
    val billingEvents: SharedFlow<BillingEvent>

    /**
     * Queries details (such as pricing, description, and offer tokens) from Google Play Billing for the
     * registered subscription plans (e.g. monthly and yearly).
     * Ensures the billing connection is active before performing the query.
     *
     * @return A [UiState] wrapper wrapping the list of fetched [ProductDetails] or an error wrapper.
     */
    suspend fun queryProductDetails(): UiState<List<ProductDetails>>

    /**
     * Performs an asynchronous query of current active purchases on Google Play and updates the local
     * premium persistence state accordingly. Automatically initiates the billing connection first.
     */
    fun checkSubscription()

    /**
     * Launches the Google Play purchase overlay flow for the specified product.
     *
     * @param activity The host [Activity] context required by the Play Billing client to overlay the purchase dialog.
     * @param productDetails The [ProductDetails] of the product to purchase.
     */
    fun purchaseProduct(activity: Activity, productDetails: ProductDetails)

}

