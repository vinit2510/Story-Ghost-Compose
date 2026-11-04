package `in`.vinit.storyghost.data.remote.user

import `in`.vinit.storyghost.data.model.SearchUserResponse
import `in`.vinit.storyghost.data.model.UserInfoResponse
import `in`.vinit.storyghost.data.user.UserRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class UserRepositoryTest {

    private val api: UserAPI = mockk()
    private lateinit var repository: UserRepositoryImpl

    @Before
    fun setUp() {
        repository = UserRepositoryImpl(api)
    }

    @Test
    fun `getUserInfoByUserId delegates to API with userId`() = runTest {
        val userId = "123456"
        val expectedResponse = UserInfoResponse()

        coEvery { api.getUserInfoByUserId(userId) } returns expectedResponse

        val result = repository.getUserInfoByUserId(userId)

        assertNotNull(result)
        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { api.getUserInfoByUserId(userId) }
    }

    @Test
    fun `searchUsersByQuery delegates to API with query string`() = runTest {
        val query = "test_user"
        val expectedResponse = SearchUserResponse(list = emptyList())

        coEvery {
            api.searchUsersByQuery(
                query = query,
                context = "blended",
                searchSurface = "top_search_page"
            )
        } returns expectedResponse

        val result = repository.searchUsersByQuery(query)

        assertNotNull(result)
        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) {
            api.searchUsersByQuery(
                query = query,
                context = "blended",
                searchSurface = "top_search_page"
            )
        }
    }
}
