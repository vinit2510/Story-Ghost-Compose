package `in`.vinit.storyghost.ui.navigation

import android.net.Uri
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class RouteDeepLinkTest {

    @Before
    fun setUp() {
        mockkStatic(Uri::class)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    private fun mockUri(rawUrl: String, scheme: String?, host: String?, queryParams: Map<String, String> = emptyMap()): Uri {
        val uri = mockk<Uri>(relaxed = true)
        every { Uri.parse(rawUrl) } returns uri
        every { uri.scheme } returns scheme
        every { uri.host } returns host
        every { uri.path } returns null
        queryParams.forEach { (key, value) ->
            every { uri.getQueryParameter(key) } returns value
            every { uri.getBooleanQueryParameter(key, any()) } returns (value.equals("true", ignoreCase = true))
        }
        return uri
    }

    @Test
    fun `fromDeepLink returns null for blank or invalid scheme`() {
        assertNull(Route.fromDeepLink(null))
        assertNull(Route.fromDeepLink(""))

        mockUri("https://google.com", "https", "google.com")
        assertNull(Route.fromDeepLink("https://google.com"))
    }

    @Test
    fun `fromDeepLink returns Subscription route`() {
        mockUri("storyghost://subscription", "storyghost", "subscription")
        assertEquals(Route.Subscription, Route.fromDeepLink("storyghost://subscription"))
    }

    @Test
    fun `fromDeepLink returns Downloads route`() {
        mockUri("storyghost://downloads", "storyghost", "downloads")
        assertEquals(Route.Downloads, Route.fromDeepLink("storyghost://downloads"))
    }

    @Test
    fun `fromDeepLink returns Home route`() {
        mockUri("storyghost://home", "storyghost", "home")
        assertEquals(Route.Home, Route.fromDeepLink("storyghost://home"))
    }

    @Test
    fun `fromDeepLink returns Story route`() {
        mockUri(
            "storyghost://story?id=123&isHighlight=true",
            "storyghost",
            "story",
            mapOf("id" to "123", "isHighlight" to "true")
        )
        val route = Route.fromDeepLink("storyghost://story?id=123&isHighlight=true")
        assertEquals(Route.Story("123", isHighlight = true), route)
    }

    @Test
    fun `fromDeepLink returns Highlight route`() {
        mockUri(
            "storyghost://highlight?userId=456&userName=ghost",
            "storyghost",
            "highlight",
            mapOf("userId" to "456", "userName" to "ghost")
        )
        val route = Route.fromDeepLink("storyghost://highlight?userId=456&userName=ghost")
        assertEquals(Route.Highlight(userId = "456", userName = "ghost"), route)
    }
}
