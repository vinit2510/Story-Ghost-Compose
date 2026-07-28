package `in`.vinit.storyghost.di

import org.koin.core.module.Module

val appModule: List<Module> = listOf(
    coreModule,
    networkModule,
    repositoryModule,
    adModule,
    viewModelModule
)
