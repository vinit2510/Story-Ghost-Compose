package `in`.vinit.storyghost.ui.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import `in`.vinit.storyghost.core.ui.R
import `in`.vinit.storyghost.ui.component.DashedDivider
import `in`.vinit.storyghost.ui.component.OpenSourceNoticeCard
import `in`.vinit.storyghost.ui.component.PrimaryButton
import `in`.vinit.storyghost.ui.theme.StoryGhostTheme

@Composable
fun OnboardingLoginPage(
    isCookieLoginEnabled: Boolean,
    onLoginClick: () -> Unit,
    onCookieLoginClick: () -> Unit,
    openSourceUrl: String = ""
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp)
            .verticalScroll(rememberScrollState(), reverseScrolling = true),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(Modifier.size(50.dp))

        AccountTypeSection(
            R.drawable.avatar_real_user,
            R.string.real_user_name,
            R.string.real_name,
            R.string.real_account_login_msg
        )

        DashedDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            MaterialTheme.colorScheme.tertiary
        )

        AccountTypeSection(
            R.drawable.avatar_instagram_user,
            R.string.fake10,
            R.string.fake_user,
            R.string.fake_account_login_msg
        )

        PrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp),
            text = stringResource(R.string.login_with_ig),
            color = MaterialTheme.colorScheme.surface
        ) {
            onLoginClick()
        }

        if (isCookieLoginEnabled) {
            Row(
                modifier = Modifier
                    .padding(top = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HorizontalDivider(
                    Modifier.size(width = 60.dp, height = 1.dp),
                    color = MaterialTheme.colorScheme.tertiary
                )

                Text(
                    text = stringResource(R.string.or),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )

                HorizontalDivider(
                    Modifier.size(width = 60.dp, height = 1.dp),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Text(
                text = stringResource(R.string.login_with_cookie),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .clickable { onCookieLoginClick() },
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = TextDecoration.Underline
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (openSourceUrl.isNotBlank()) {
            OpenSourceNoticeCard(
                githubUrl = openSourceUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 30.dp)
            )
        }

    }

}

@Composable
private fun AccountTypeSection(
    @DrawableRes image: Int,
    @StringRes username: Int,
    @StringRes name: Int,
    @StringRes description: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(25.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            Image(
                painterResource(image),
                "Profile photo",
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape),
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    stringResource(username),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    stringResource(name),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        Text(
            stringResource(description),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}


@Preview
@Composable
private fun OnboardingLoginPagePreview() {
    StoryGhostTheme {
        OnboardingLoginPage(
            isCookieLoginEnabled = true,
            openSourceUrl = "https://github.com/username/project",
            onLoginClick = {},
            onCookieLoginClick = {}
        )
    }
}