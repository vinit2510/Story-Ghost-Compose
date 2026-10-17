package `in`.vinit.storyghost.ui.subscription

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.vinit.storyghost.core.model.SubscriptionPlan
import `in`.vinit.storyghost.core.ui.R
import `in`.vinit.storyghost.data.billing.BillingEvent
import `in`.vinit.storyghost.data.billing.BillingRepository
import `in`.vinit.storyghost.data.billing.MONTHLY_PLAN_ID
import `in`.vinit.storyghost.data.billing.YEARLY_PLAN_ID
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.UiText
import `in`.vinit.storyghost.ui.toUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel managing the in-app subscription screen and Google Play Billing flows.
 *
 * Responsibilities:
 * - Queries subscription pricing tiers (Monthly and Yearly) via [BillingRepository.queryProductDetails].
 * - Observes hot [BillingEvent] streams (success, cancellation, errors) from Google Play.
 * - Handles user plan selection and launches purchase flows via [BillingRepository.purchaseProduct].
 */
class SubscriptionViewModel(
    private val billingRepository: BillingRepository
) : ViewModel() {

    enum class Plan {
        MONTHLY,
        YEARLY
    }

    private val _billingErrorEvent = MutableSharedFlow<UiText>()
    val billingErrorEvent: SharedFlow<UiText> = _billingErrorEvent.asSharedFlow()

    private val _selectedPlan = MutableStateFlow(Plan.YEARLY)
    val selectedPlan: StateFlow<Plan> = _selectedPlan.asStateFlow()

    private val _subscriptionPlans =
        MutableStateFlow<UiState<List<SubscriptionPlan>>>(UiState.Loading)
    val subscriptionPlans: StateFlow<UiState<List<SubscriptionPlan>>> =
        _subscriptionPlans.asStateFlow()

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
            _subscriptionPlans.value = UiState.Loading
            _subscriptionPlans.value = billingRepository.queryProductDetails().toUiState()
        }
    }

    fun selectPlan(plan: Plan) {
        _selectedPlan.value = plan
    }

    fun subscribe(activity: Activity) {
        val state = _subscriptionPlans.value
        if (state is UiState.Success) {
            val targetId = when (_selectedPlan.value) {
                Plan.MONTHLY -> MONTHLY_PLAN_ID
                Plan.YEARLY -> YEARLY_PLAN_ID
            }
            val plan = state.data.find { it.productId == targetId }
            if (plan != null) {
                billingRepository.purchaseProduct(activity, plan)
            }
        }
    }
}
