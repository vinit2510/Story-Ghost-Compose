package `in`.vinit.storyghost.ui.login

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val loginFeatureModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::LoginDialogViewModel)
    viewModelOf(::LogoutDialogViewModel)
}
