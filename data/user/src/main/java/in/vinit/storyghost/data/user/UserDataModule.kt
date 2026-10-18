package `in`.vinit.storyghost.data.user

import `in`.vinit.storyghost.data.remote.user.UserAPI
import org.koin.dsl.module
import retrofit2.Retrofit

val userDataModule = module {
    single {
        get<Retrofit>().create(UserAPI::class.java)
    }

    single<UserRepository> {
        UserRepositoryImpl(get(), get())
    }
}
