package `in`.vinit.storyghost.data.remote.user

import `in`.vinit.storyghost.data.model.SearchUserResponse
import `in`.vinit.storyghost.data.model.UserInfoResponse
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
    fun `getUserInfo delegates to API with serialized variables`() = runTest {
        val userId = "123456"
        val expectedResponse = UserInfoResponse()

        coEvery { api.getUserInfo(any(), any()) } returns expectedResponse

        val result = repository.getUserInfo(userId)

        assertNotNull(result)
        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { api.getUserInfo(any(), any()) }
    }

    @Test
    fun `searchUser delegates to API with query string`() = runTest {
        val query = "test_user"
        val expectedResponse = SearchUserResponse(users = arrayListOf())

        coEvery { api.searchUser(query) } returns expectedResponse

        val result = repository.searchUser(query)

        assertNotNull(result)
        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { api.searchUser(query) }
    }
}
