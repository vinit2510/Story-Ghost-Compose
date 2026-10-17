package `in`.vinit.storyghost.data.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryProductDetails
import `in`.vinit.storyghost.core.common.Logger
import `in`.vinit.storyghost.core.model.SubscriptionPlan
import `in`.vinit.storyghost.data.local.LocalDataSource
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Currency
import java.util.Locale

private const val TAG = "BillingRepository"

class BillingRepositoryImpl(
    context: Context,
    private val localDataSource: LocalDataSource,
    private val logger: Logger
) : BillingRepository, PurchasesUpdatedListener {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _billingEvents = MutableSharedFlow<BillingEvent>()
    override val billingEvents: SharedFlow<BillingEvent> = _billingEvents.asSharedFlow()

    private val connectionMutex = Mutex()
    private var connectionDeferred: CompletableDeferred<Boolean>? = null

    @Volatile
    private var cachedProductDetails: List<ProductDetails> = emptyList()

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .enablePrepaidPlans()
                .build()
        )
        .build()

    private suspend fun connectToBilling(): Boolean {
        logger.d(TAG, "connectToBilling: ")
        if (billingClient.isReady) return true

        return connectionMutex.withLock {
            if (billingClient.isReady) return@withLock true

            val deferred = connectionDeferred ?: CompletableDeferred<Boolean>().also {
                connectionDeferred = it
                logger.d(TAG, "Starting billing connection...")
                billingClient.startConnection(object : BillingClientStateListener {
                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        val success = billingResult.responseCode == BillingResponseCode.OK
                        if (success) {
                            logger.d(TAG, "Billing setup finished successfully")
                        } else {
                            logger.e(
                                TAG,
                                "Billing setup failed with response code: ${billingResult.responseCode}"
                            )
                        }
                        it.complete(success)
                    }

                    override fun onBillingServiceDisconnected() {
                        logger.w(TAG, "Billing service disconnected")
                        it.complete(false)
                        scope.launch {
                            connectionMutex.withLock {
                                connectionDeferred = null
                            }
                            _billingEvents.emit(BillingEvent.ServiceDisconnected)
                        }
                    }
                })
            }

            try {
                deferred.await()
            } finally {
                if (!billingClient.isReady) {
                    connectionDeferred = null
                }
            }
        }
    }

    override suspend fun queryProductDetails(): Result<List<SubscriptionPlan>> {
        logger.d(TAG, "queryProductDetails: ")
        val connected = connectToBilling()
        if (!connected) {
            return Result.failure(Exception("Billing service disconnected"))
        }

        val queryParams = QueryProductDetailsParams.newBuilder().setProductList(
            listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(MONTHLY_PLAN_ID)
                    .setProductType(ProductType.SUBS)
                    .build(),
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(YEARLY_PLAN_ID)
                    .setProductType(ProductType.SUBS)
                    .build()
            )
        ).build()

        return try {
            val result = billingClient.queryProductDetails(queryParams)
            val billingResult = result.billingResult
            val productDetailsList = result.productDetailsList
            if (billingResult.responseCode == BillingResponseCode.OK && productDetailsList != null) {
                cachedProductDetails = productDetailsList
                val plans = productDetailsList.map { product ->
                    val pricingPhase = product.subscriptionOfferDetails?.getOrNull(0)
                        ?.pricingPhases?.pricingPhaseList?.getOrNull(0)

                    val formattedPrice = pricingPhase?.formattedPrice ?: "-"

                    val effectiveMonthlyPrice =
                        if (product.productId == YEARLY_PLAN_ID && pricingPhase != null) {
                            val yearlyPriceValue =
                                pricingPhase.priceAmountMicros.toDouble() / 1_000_000.0
                            val monthlyEffective = yearlyPriceValue / 12.0
                            val currencyCode = pricingPhase.priceCurrencyCode
                            val symbol = try {
                                Currency.getInstance(currencyCode).symbol
                            } catch (_: Exception) {
                                "$"
                            }
                            val formatted =
                                String.format(Locale.getDefault(), "%.2f", monthlyEffective)
                            "$symbol$formatted"
                        } else {
                            null
                        }

                    SubscriptionPlan(
                        productId = product.productId,
                        formattedPrice = formattedPrice,
                        effectiveMonthlyPrice = effectiveMonthlyPrice
                    )
                }
                logger.d(
                    TAG,
                    "Successfully queried product details: ${plans.size} items found"
                )
                Result.success(plans)
            } else {
                logger.e(TAG, "Failed to query product details: ${billingResult.debugMessage}")
                Result.failure(Exception("Failed to get subscription details"))
            }
        } catch (e: Exception) {
            logger.e(TAG, "Error querying products", e)
            Result.failure(Exception("Failed to get subscription details"))
        }
    }

    override fun checkSubscription() {
        logger.d(TAG, "checkSubscription: ")
        scope.launch {
            val connected = connectToBilling()
            if (connected) {
                val queryParams = QueryPurchasesParams.newBuilder()
                    .setProductType(ProductType.SUBS)
                    .build()

                billingClient.queryPurchasesAsync(queryParams) { billingResult, purchaseList ->
                    if (billingResult.responseCode == BillingResponseCode.OK) {
                        val activePurchases = purchaseList.filter {
                            it.purchaseState == Purchase.PurchaseState.PURCHASED
                        }
                        logger.d(
                            TAG,
                            "Checked purchases. Active purchases: ${activePurchases.size}"
                        )
                        scope.launch {
                            if (activePurchases.isNotEmpty()) {
                                activePurchases.forEach { purchase ->
                                    handlePurchase(purchase)
                                }
                            } else {
                                localDataSource.setPremium(false)
                                localDataSource.savePlanId(null)
                            }
                        }
                    } else {
                        logger.e(TAG, "Failed to query purchases: ${billingResult.debugMessage}")
                    }
                }
            } else {
                logger.e(TAG, "checkSubscription: billing client not connected")
            }
        }
    }

    override fun purchaseProduct(
        activity: Activity,
        plan: SubscriptionPlan
    ) {
        val productDetails = cachedProductDetails.find { it.productId == plan.productId }
        if (productDetails == null) {
            logger.e(TAG, "purchaseProduct: productDetails not found for ${plan.productId}")
            return
        }
        logger.d(TAG, "purchaseProduct: ${productDetails.productId}")
        val offerToken = productDetails.subscriptionOfferDetails?.getOrNull(0)?.offerToken ?: ""
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        when (billingResult.responseCode) {
            BillingResponseCode.OK if purchases != null -> {
                logger.d(
                    TAG,
                    "Purchases updated successfully. Processing ${purchases.size} purchases."
                )
                purchases.forEach { purchase ->
                    handlePurchase(purchase)
                }
            }

            BillingResponseCode.USER_CANCELED -> {
                logger.d(TAG, "User canceled purchase flow")
                scope.launch {
                    _billingEvents.emit(BillingEvent.PurchaseCanceled)
                }
            }

            else -> {
                logger.e(
                    TAG,
                    "Purchases update failed: ${billingResult.debugMessage}[${billingResult.responseCode}]"
                )
                scope.launch {
                    _billingEvents.emit(BillingEvent.PurchaseError(billingResult.debugMessage))
                }
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        logger.d(TAG, "handlePurchase: ")
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (purchase.isAcknowledged) {
                scope.launch {
                    localDataSource.setPremium(true)
                    localDataSource.savePlanId(purchase.products.firstOrNull())
                    _billingEvents.emit(BillingEvent.PurchaseSuccess)
                }
            } else {
                acknowledgePurchase(purchase)
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        logger.d(TAG, "acknowledgePurchase: ")
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            if (billingResult.responseCode == BillingResponseCode.OK) {
                logger.d(TAG, "Purchase acknowledged successfully")
                scope.launch {
                    localDataSource.setPremium(true)
                    localDataSource.savePlanId(purchase.products.firstOrNull())
                    _billingEvents.emit(BillingEvent.PurchaseSuccess)
                }
            } else {
                logger.e(TAG, "Failed to acknowledge purchase: ${billingResult.debugMessage}")
                scope.launch {
                    _billingEvents.emit(BillingEvent.PurchaseError(billingResult.debugMessage))
                }
            }
        }
    }

}
