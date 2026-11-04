package `in`.vinit.storyghost.data.model

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UserStoryResponseTest {

    private val gson = Gson()

    @Test
    fun `allReelsMedia returns items from native mobile reels_media array format`() {
        val json = """
            {
              "reels_media": [
                {
                  "id": "29142288954",
                  "latest_reel_media": 1793711774,
                  "items": [
                    {
                      "id": "item_native_1",
                      "pk": "item_native_1",
                      "media_type": 1,
                      "taken_at": 1793711774
                    }
                  ]
                }
              ],
              "status": "ok"
            }
        """.trimIndent()

        val response = gson.fromJson(json, UserStoryResponse::class.java)

        assertEquals(1, response.allReelsMedia.size)
        val reel = response.allReelsMedia.first()
        assertEquals("29142288954", reel.id)
        assertEquals(1, reel.items?.size)
        assertEquals("item_native_1", reel.items?.first()?.pk)
        assertEquals("ok", response.status)
    }

    @Test
    fun `allReelsMedia returns items from native mobile reels map format`() {
        val json = """
            {
              "reels": {
                "user_map_1": {
                  "id": "user_map_1",
                  "items": [
                    {
                      "id": "item_map_1",
                      "pk": "item_map_1",
                      "media_type": 2
                    }
                  ]
                }
              },
              "status": "ok"
            }
        """.trimIndent()

        val response = gson.fromJson(json, UserStoryResponse::class.java)

        assertEquals(1, response.allReelsMedia.size)
        val reel = response.allReelsMedia.first()
        assertEquals("user_map_1", reel.id)
        assertEquals(1, reel.items?.size)
        assertEquals("item_map_1", reel.items?.first()?.pk)
    }

    @Test
    fun `allReelsMedia returns empty list when reels_media and reels are null`() {
        val emptyResponse = UserStoryResponse()
        assertTrue(emptyResponse.allReelsMedia.isEmpty())
    }
}
