package `in`.vinit.storyghost.di

import `in`.vinit.storyghost.ui.downloads.DownloadsViewModel
import `in`.vinit.storyghost.ui.highlight.HighlightViewModel
import `in`.vinit.storyghost.ui.home.HomeViewModel
import `in`.vinit.storyghost.ui.login.LoginDialogViewModel
import `in`.vinit.storyghost.ui.login.LoginViewModel
import `in`.vinit.storyghost.ui.login.LogoutDialogViewModel
import `in`.vinit.storyghost.ui.navigation.StartupViewModel
import `in`.vinit.storyghost.ui.onboarding.OnboardingViewModel
import `in`.vinit.storyghost.ui.story.StoryViewModel
import `in`.vinit.storyghost.ui.subscription.SubscriptionViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule: Module = module {
    viewModelOf(::StartupViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::LoginDialogViewModel)
    viewModelOf(::LogoutDialogViewModel)
    viewModelOf(::HighlightViewModel)
    viewModelOf(::StoryViewModel)
    viewModelOf(::DownloadsViewModel)
    viewModelOf(::SubscriptionViewModel)
}
