package `in`.vinit.storyghost.data.remote.auth

import `in`.vinit.storyghost.data.local.LocalDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class LogoutAuthenticator(private val localDataSource: LocalDataSource) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        runBlocking {
            localDataSource.clearAuthentication()
        }
        return null
    }
}
