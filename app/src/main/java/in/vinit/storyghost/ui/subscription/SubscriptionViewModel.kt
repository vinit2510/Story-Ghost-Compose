package `in`.vinit.storyghost.ui.subscription

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import `in`.vinit.storyghost.R
import `in`.vinit.storyghost.data.remote.billing.BillingEvent
import `in`.vinit.storyghost.data.remote.billing.BillingRepository
import `in`.vinit.storyghost.data.remote.billing.MONTHLY_PLAN_ID
import `in`.vinit.storyghost.data.remote.billing.YEARLY_PLAN_ID
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.UiText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SubscriptionViewModel(
    private val billingRepository: BillingRepository
) : ViewModel() {

    private val _billingErrorEvent = MutableSharedFlow<UiText>()
    val billingErrorEvent: SharedFlow<UiText> = _billingErrorEvent.asSharedFlow()

    enum class Plan {
        MONTHLY,
        YEARLY
    }

    private val _selectedPlan = MutableStateFlow(Plan.YEARLY)
    val selectedPlan: StateFlow<Plan> = _selectedPlan.asStateFlow()

    private val _productDetails = MutableStateFlow<UiState<List<ProductDetails>>>(UiState.Loading)
    val productDetails: StateFlow<UiState<List<ProductDetails>>> = _productDetails.asStateFlow()

    private val _purchaseSuccessEvent = MutableSharedFlow<Plan>()
    val purchaseSuccessEvent: SharedFlow<Plan> = _purchaseSuccessEvent.asSharedFlow()

    init {
        loadProductDetails()

        viewModelScope.launch {
            billingRepository.billingEvents.collect { event ->
                when (event) {
                    is BillingEvent.PurchaseSuccess -> {
                        _purchaseSuccessEvent.emit(_selectedPlan.value)
                    }

                    is BillingEvent.ServiceDisconnected -> {
                        _billingErrorEvent.emit(UiText.StringResource(R.string.error_billing_service_disconnected))
                    }

                    is BillingEvent.PurchaseCanceled -> {
                        _billingErrorEvent.emit(UiText.StringResource(R.string.error_billing_purchase_canceled))
                    }

                    is BillingEvent.PurchaseError -> {
                        _billingErrorEvent.emit(UiText.StringResource(R.string.error_billing_unknown))
                    }
                }
            }
        }
    }

    fun loadProductDetails() {
        viewModelScope.launch {
            _productDetails.value = UiState.Loading
            _productDetails.value = billingRepository.queryProductDetails()
        }
    }

    fun selectPlan(plan: Plan) {
        _selectedPlan.value = plan
    }

    fun subscribe(activity: Activity) {
        val detailsState = productDetails.value
        if (detailsState is UiState.Success) {
            val planId = when (_selectedPlan.value) {
                Plan.MONTHLY -> MONTHLY_PLAN_ID
                Plan.YEARLY -> YEARLY_PLAN_ID
            }
            val details = detailsState.data.find { it.productId == planId }
            if (details != null) {
                billingRepository.purchaseProduct(activity, details)
            }
        }
    }
}
