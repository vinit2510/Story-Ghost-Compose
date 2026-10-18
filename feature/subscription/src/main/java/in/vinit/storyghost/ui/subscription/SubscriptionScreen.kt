package `in`.vinit.storyghost.ui.subscription

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.vinit.storyghost.core.model.SubscriptionPlan
import `in`.vinit.storyghost.core.ui.R
import `in`.vinit.storyghost.data.billing.MONTHLY_PLAN_ID
import `in`.vinit.storyghost.data.billing.YEARLY_PLAN_ID
import `in`.vinit.storyghost.ui.UiState
import `in`.vinit.storyghost.ui.component.ErrorPlaceholder
import `in`.vinit.storyghost.ui.theme.StoryGhostTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SubscriptionViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val selectedPlan by viewModel.selectedPlan.collectAsStateWithLifecycle()
    val subscriptionPlansState by viewModel.subscriptionPlans.collectAsStateWithLifecycle()

    val showSuccessDialogPlan = remember { mutableStateOf<SubscriptionViewModel.Plan?>(null) }

    LaunchedEffect(viewModel.purchaseSuccessEvent) {
        viewModel.purchaseSuccessEvent.collect { plan ->
            showSuccessDialogPlan.value = plan
        }
    }

    LaunchedEffect(viewModel.billingErrorEvent) {
        viewModel.billingErrorEvent.collect { errorMsg ->
            Toast.makeText(context, errorMsg.asString(context), Toast.LENGTH_SHORT).show()
        }
    }

    showSuccessDialogPlan.value?.let { plan ->
        SubscriptionSuccessDialog(
            plan = plan,
            onDismiss = {
                showSuccessDialogPlan.value = null
                onBackClick()
            }
        )
    }

    SubscriptionContent(
        selectedPlan = selectedPlan,
        subscriptionPlansState = subscriptionPlansState,
        onBackClick = onBackClick,
        onPlanSelect = viewModel::selectPlan,
        onSubscribeClick = {
            val activity = context as? Activity
            if (activity != null) {
                viewModel.subscribe(activity)
            }
        },
        onRetryClick = { viewModel.loadProductDetails() },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionContent(
    selectedPlan: SubscriptionViewModel.Plan,
    subscriptionPlansState: UiState<List<SubscriptionPlan>>,
    onBackClick: () -> Unit,
    onPlanSelect: (SubscriptionViewModel.Plan) -> Unit,
    onSubscribeClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.upgrade_to_premium),
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                actions = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = "Close"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.get_more_from_story_ghost),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (subscriptionPlansState) {
                    is UiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    is UiState.Error -> {
                        ErrorPlaceholder(
                            message = subscriptionPlansState.errorMsg.asString(),
                            modifier = Modifier.align(Alignment.Center),
                            onRetryClick = onRetryClick
                        )
                    }

                    is UiState.Success -> {
                        SubscriptionPlansList(
                            subscriptionPlans = subscriptionPlansState.data,
                            selectedPlan = selectedPlan,
                            onPlanSelect = onPlanSelect
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.subscription_auto_renews_on_a_recurring_basis),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.cancel_anytime),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onSubscribeClick,
                    enabled = subscriptionPlansState is UiState.Success,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                ) {
                    Text(
                        text = stringResource(R.string.subscribe_now),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
fun SubscriptionPlansList(
    subscriptionPlans: List<SubscriptionPlan>,
    selectedPlan: SubscriptionViewModel.Plan,
    onPlanSelect: (SubscriptionViewModel.Plan) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val monthlyPlan = subscriptionPlans.find { it.productId == MONTHLY_PLAN_ID }
        val yearlyPlan = subscriptionPlans.find { it.productId == YEARLY_PLAN_ID }

        val monthlyPrice = monthlyPlan?.formattedPrice
        val yearlyPrice = yearlyPlan?.formattedPrice

        val effectiveMonthlyText = yearlyPlan?.effectiveMonthlyPrice?.let {
            stringResource(R.string.effective_price_per_month, it)
        }

        // Monthly Card
        PlanCard(
            planName = stringResource(R.string.monthly_plan),
            priceText = stringResource(R.string.price_per_month, monthlyPrice ?: "-"),
            effectivePriceText = null,
            features = listOf(
                stringResource(R.string.ad_free_experience),
                stringResource(R.string.pre_unlock_the_mention_count_of_story)
            ),
            isSelected = selectedPlan == SubscriptionViewModel.Plan.MONTHLY,
            showBestValue = false,
            onClick = { onPlanSelect(SubscriptionViewModel.Plan.MONTHLY) }
        )

        // Yearly Card
        PlanCard(
            planName = stringResource(R.string.yearly_plan),
            priceText = stringResource(R.string.price_per_year, yearlyPrice ?: "-"),
            effectivePriceText = effectiveMonthlyText,
            features = listOf(
                stringResource(R.string.ad_free_experience),
                stringResource(R.string.early_access_to_new_feature),
                stringResource(R.string.pre_unlock_the_mention_count_of_story)
            ),
            isSelected = selectedPlan == SubscriptionViewModel.Plan.YEARLY,
            showBestValue = true,
            onClick = { onPlanSelect(SubscriptionViewModel.Plan.YEARLY) }
        )
    }
}

@Composable
fun PlanCard(
    planName: String,
    priceText: String,
    effectivePriceText: String?,
    features: List<String>,
    isSelected: Boolean,
    showBestValue: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val borderStroke = BorderStroke(2.dp, borderColor)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        border = borderStroke,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Checkbox Indicator
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            painter = painterResource(R.drawable.ic_correct_tick),
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = planName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (showBestValue) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = stringResource(R.string.best_value),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = priceText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(start = 40.dp)
            )

            if (effectivePriceText != null) {
                Text(
                    text = effectivePriceText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(start = 40.dp, top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            features.forEach { feature ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_tick),
                        contentDescription = "Feature Tick",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SubscriptionScreenPreview() {
    StoryGhostTheme {
        SubscriptionContent(
            selectedPlan = SubscriptionViewModel.Plan.YEARLY,
            subscriptionPlansState = UiState.Success(emptyList()),
            onBackClick = {},
            onPlanSelect = {},
            onSubscribeClick = {},
            onRetryClick = {}
        )
    }
}
