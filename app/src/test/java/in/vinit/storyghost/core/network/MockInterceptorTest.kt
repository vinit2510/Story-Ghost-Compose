package `in`.vinit.storyghost.core.network

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream

class MockInterceptorTest {

    private val context: Context = mockk(relaxed = true)
    private lateinit var interceptor: MockInterceptor

    @Before
    fun setUp() {
        interceptor = MockInterceptor(context)
    }

    @Test
    fun `intercept returns mock response for search topsearch endpoint`() {
        val mockJson = """{"users": []}"""
        every { context.assets.open("mock_search_user.json") } returns ByteArrayInputStream(mockJson.toByteArray())

        val chain = mockk<Interceptor.Chain>()
        val request = Request.Builder()
            .url("https://example.com/web/search/topsearch/?query=test")
            .build()

        every { chain.request() } returns request

        val response: Response = interceptor.intercept(chain)

        assertNotNull(response)
        assertEquals(200, response.code)
        assertNotNull(response.body)
    }

    @Test
    fun `intercept passes through unhandled endpoints to chain proceed`() {
        val chain = mockk<Interceptor.Chain>()
        val request = Request.Builder()
            .url("https://example.com/api/v1/unhandled/")
            .build()

        val expectedResponse = mockk<Response>()

        every { chain.request() } returns request
        every { chain.proceed(request) } returns expectedResponse

        val response = interceptor.intercept(chain)

        assertEquals(expectedResponse, response)
    }
}
