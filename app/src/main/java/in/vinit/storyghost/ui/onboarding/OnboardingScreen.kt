package `in`.vinit.storyghost.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import `in`.vinit.storyghost.R
import `in`.vinit.storyghost.ui.theme.StoryGhostTheme
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(modifier: Modifier = Modifier) {

    val pagerState = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()

    Scaffold(content = {
        HorizontalPager(
            state = pagerState,
            modifier = modifier
                .fillMaxSize()
                .padding(it)
        ) { page ->

            when (page) {
                0 -> OnboardingCommonPage(
                    R.drawable.img_onboarding_1,
                    R.string.onboarding_text_1,
                )

                1 -> OnboardingCommonPage(
                    R.drawable.img_onboarding_2,
                    R.string.onboarding_text_2
                )

                2 -> OnboardingCommonPage(
                    R.drawable.img_onboarding_3,
                    R.string.onboarding_text_3
                )

                3 -> OnboardingLoginPage()
            }

        }


    }, bottomBar = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, bottom = 10.dp, end = 15.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(R.string.skip),
                modifier = Modifier.padding(vertical = 20.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary
            )

            Row {
                repeat(pagerState.pageCount) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.tertiary
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(10.dp)
                    )
                }
            }

            Button(
                onClick = {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }, modifier = Modifier
                    .size(60.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp)
            ) {
                val icon =
                    if (pagerState.currentPage == 3) R.drawable.ic_correct_tick else R.drawable.ic_arrow_right
                Image(
                    painterResource(icon),
                    "Next",
                )
            }
        }
    })

}


@Preview
@Composable
private fun OnboardingScreenPreview() {
    StoryGhostTheme {
        OnboardingScreen()
    }
}