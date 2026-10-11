package `in`.vinit.storyghost.ui.subscription

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import `in`.vinit.storyghost.core.ui.R
import `in`.vinit.storyghost.ui.component.PrimaryButton
import `in`.vinit.storyghost.ui.theme.StoryGhostTheme

@Composable
fun SubscriptionSuccessDialog(
    plan: SubscriptionViewModel.Plan,
    onDismiss: () -> Unit
) {
    val planText = when (plan) {
        SubscriptionViewModel.Plan.MONTHLY -> stringResource(R.string.monthly)
        SubscriptionViewModel.Plan.YEARLY -> stringResource(R.string.yearly)
    }

    Dialog(onDismissRequest = onDismiss) {
        SubscriptionSuccessDialogContent(
            planText = planText,
            onDismiss = onDismiss
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionSuccessDialogContent(
    planText: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.vector_confetti),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.4f,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)),
                modifier = Modifier.matchParentSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_unlock),
                    contentDescription = null,
                    modifier = Modifier.padding(top = 20.dp)
                )

                Text(
                    text = stringResource(R.string.plan_activated, planText),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 30.dp, start = 16.dp, end = 16.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))

                PrimaryButton(
                    text = stringResource(R.string.continue_),
                    color = MaterialTheme.colorScheme.background,
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SubscriptionSuccessDialogPreview() {
    StoryGhostTheme {
        SubscriptionSuccessDialogContent(
            planText = "Yearly",
            onDismiss = {}
        )
    }
}
