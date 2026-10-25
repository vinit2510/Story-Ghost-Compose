package `in`.vinit.storyghost.core.notification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NotificationPayloadTest {

    @Test
    fun `fromData parses basic fields and flat actions correctly`() {
        val data = mapOf(
            "title" to "Special Offer",
            "body" to "Upgrade now for 50% off",
            "channel_id" to "promotions",
            "launch_url" to "storyghost://subscription",
            "image_url" to "https://example.com/banner.png",
            "icon_url" to "https://example.com/avatar.png",
            "btn1_text" to "Upgrade",
            "btn1_url" to "storyghost://subscription",
            "btn2_text" to "Later",
            "btn2_url" to "storyghost://home",
            "constraint_min_version" to "10",
            "constraint_max_version" to "50",
            "constraint_tier" to "FREE_ONLY",
            "constraint_auth" to "LOGGED_IN_ONLY"
        )

        val payload = NotificationPayload.fromData(data)

        assertEquals("Special Offer", payload.title)
        assertEquals("Upgrade now for 50% off", payload.body)
        assertEquals("promotions", payload.channelId)
        assertEquals("storyghost://subscription", payload.launchUrl)
        assertEquals("https://example.com/banner.png", payload.imageUrl)
        assertEquals("https://example.com/avatar.png", payload.iconUrl)

        assertEquals(2, payload.actions.size)
        assertEquals("Upgrade", payload.actions[0].title)
        assertEquals("storyghost://subscription", payload.actions[0].url)
        assertEquals("Later", payload.actions[1].title)
        assertEquals("storyghost://home", payload.actions[1].url)

        assertEquals(10, payload.constraints.minVersionCode)
        assertEquals(50, payload.constraints.maxVersionCode)
        assertEquals("FREE_ONLY", payload.constraints.targetTier)
        assertEquals("LOGGED_IN_ONLY", payload.constraints.targetAuth)
    }

    @Test
    fun `fromData falls back to defaults when optional fields are missing`() {
        val data = mapOf(
            "title" to "Hello",
            "body" to "World"
        )

        val payload = NotificationPayload.fromData(data)

        assertEquals("Hello", payload.title)
        assertEquals("World", payload.body)
        assertEquals(NotificationChannels.CHANNEL_GENERAL_ID, payload.channelId)
        assertNull(payload.launchUrl)
        assertNull(payload.imageUrl)
        assertNull(payload.iconUrl)
        assertEquals(0, payload.actions.size)
        assertNull(payload.constraints.minVersionCode)
        assertNull(payload.constraints.maxVersionCode)
        assertNull(payload.constraints.targetTier)
        assertNull(payload.constraints.targetAuth)
    }
}
