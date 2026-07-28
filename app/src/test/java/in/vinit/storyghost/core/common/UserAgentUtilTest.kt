package `in`.vinit.storyghost.core.common

import android.content.Context
import io.mockk.mockk
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UserAgentUtilTest {

    @Test
    fun `generateInstagramUserAgent returns valid string without crashing on mock context`() {
        val mockContext = mockk<Context>(relaxed = true)
        val userAgent = UserAgentUtil.generateInstagramUserAgent(mockContext)

        assertNotNull(userAgent)
        assertTrue(userAgent.isNotEmpty())
    }
}
