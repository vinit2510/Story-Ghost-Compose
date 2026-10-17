package `in`.vinit.storyghost.data.billing

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val billingDataModule = module {
    single<BillingRepository> {
        BillingRepositoryImpl(androidContext(), get(), get())
    }
}
