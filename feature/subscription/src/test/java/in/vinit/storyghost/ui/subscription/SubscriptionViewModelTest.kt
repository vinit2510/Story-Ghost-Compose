package `in`.vinit.storyghost.ui.subscription

import android.app.Activity
import app.cash.turbine.test
import `in`.vinit.storyghost.core.model.SubscriptionPlan
import `in`.vinit.storyghost.core.ui.R
import `in`.vinit.storyghost.data.billing.BillingEvent
import `in`.vinit.storyghost.data.billing.BillingRepository
import `in`.vinit.storyghost.data.billing.MONTHLY_PLAN_ID
import `in`.vinit.storyghost.data.billing.YEARLY_PLAN_ID
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.UiText
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SubscriptionViewModelTest {

    private val billingRepository: BillingRepository = mockk(relaxed = true)
    private val billingEventsFlow = MutableSharedFlow<BillingEvent>()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: SubscriptionViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { billingRepository.billingEvents } returns billingEventsFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `default selected plan is YEARLY`() = runTest {
        viewModel = SubscriptionViewModel(billingRepository)
        assertEquals(SubscriptionViewModel.Plan.YEARLY, viewModel.selectedPlan.value)
    }

    @Test
    fun `selectPlan updates selectedPlan state`() = runTest {
        viewModel = SubscriptionViewModel(billingRepository)
        viewModel.selectPlan(SubscriptionViewModel.Plan.MONTHLY)
        assertEquals(SubscriptionViewModel.Plan.MONTHLY, viewModel.selectedPlan.value)
    }

    @Test
    fun `loadProductDetails updates subscriptionPlans to Success`() = runTest {
        val mockPlans = listOf(
            SubscriptionPlan(productId = MONTHLY_PLAN_ID, formattedPrice = "$4.99"),
            SubscriptionPlan(productId = YEARLY_PLAN_ID, formattedPrice = "$29.99")
        )
        coEvery { billingRepository.queryProductDetails() } returns Result.success(mockPlans)

        viewModel = SubscriptionViewModel(billingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.subscriptionPlans.value
        assertTrue(state is UiState.Success)
        assertEquals(mockPlans, (state as UiState.Success).data)
    }

    @Test
    fun `loadProductDetails failure updates subscriptionPlans to Error`() = runTest {
        coEvery { billingRepository.queryProductDetails() } returns Result.failure(Exception("Network error"))

        viewModel = SubscriptionViewModel(billingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.subscriptionPlans.value
        assertTrue(state is UiState.Error)
    }

    @Test
    fun `billingEvent PurchaseSuccess emits selected plan`() = runTest {
        viewModel = SubscriptionViewModel(billingRepository)
        viewModel.selectPlan(SubscriptionViewModel.Plan.MONTHLY)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.purchaseSuccessEvent.test {
            billingEventsFlow.emit(BillingEvent.PurchaseSuccess)
            assertEquals(SubscriptionViewModel.Plan.MONTHLY, awaitItem())
        }
    }

    @Test
    fun `billingEvent ServiceDisconnected emits error event`() = runTest {
        viewModel = SubscriptionViewModel(billingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.billingErrorEvent.test {
            billingEventsFlow.emit(BillingEvent.ServiceDisconnected)
            val error = awaitItem()
            assertTrue(error is UiText.StringResource)
            assertEquals(
                R.string.error_billing_service_disconnected,
                (error as UiText.StringResource).resId
            )
        }
    }

    @Test
    fun `billingEvent PurchaseCanceled emits error event`() = runTest {
        viewModel = SubscriptionViewModel(billingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.billingErrorEvent.test {
            billingEventsFlow.emit(BillingEvent.PurchaseCanceled)
            val error = awaitItem()
            assertTrue(error is UiText.StringResource)
            assertEquals(
                R.string.error_billing_purchase_canceled,
                (error as UiText.StringResource).resId
            )
        }
    }

    @Test
    fun `billingEvent PurchaseError emits error event`() = runTest {
        viewModel = SubscriptionViewModel(billingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.billingErrorEvent.test {
            billingEventsFlow.emit(BillingEvent.PurchaseError("Billing error"))
            val error = awaitItem()
            assertTrue(error is UiText.StringResource)
            assertEquals(R.string.error_billing_unknown, (error as UiText.StringResource).resId)
        }
    }

    @Test
    fun `subscribe calls billingRepository purchaseProduct with matching plan`() = runTest {
        val monthlyPlan = SubscriptionPlan(productId = MONTHLY_PLAN_ID, formattedPrice = "$4.99")
        val yearlyPlan = SubscriptionPlan(productId = YEARLY_PLAN_ID, formattedPrice = "$29.99")
        coEvery { billingRepository.queryProductDetails() } returns Result.success(
            listOf(
                monthlyPlan,
                yearlyPlan
            )
        )

        val mockActivity: Activity = mockk(relaxed = true)
        viewModel = SubscriptionViewModel(billingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectPlan(SubscriptionViewModel.Plan.YEARLY)
        viewModel.subscribe(mockActivity)

        coVerify(exactly = 1) { billingRepository.purchaseProduct(mockActivity, yearlyPlan) }
    }
}
