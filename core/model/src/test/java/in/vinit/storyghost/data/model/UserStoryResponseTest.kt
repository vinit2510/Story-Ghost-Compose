package `in`.vinit.storyghost.data.model

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UserStoryResponseTest {

    private val gson = Gson()

    @Test
    fun `reelsMedia returns nodes from modern xdt connection format`() {
        val json = """
            {
              "data": {
                "xdt_api__v1__feed__reels_media__connection": {
                  "edges": [
                    {
                      "node": {
                        "id": "194317179",
                        "items": [
                          {
                            "id": "item_123",
                            "pk": "item_123",
                            "media_type": 1,
                            "taken_at": 1784557520
                          }
                        ]
                      }
                    }
                  ]
                }
              }
            }
        """.trimIndent()

        val response = gson.fromJson(json, UserStoryResponse::class.java)

        assertNotNull(response.data)
        assertEquals(1, response.reelsMedia.size)
        val reel = response.reelsMedia.first()
        assertEquals("194317179", reel.id)
        assertEquals(1, reel.items?.size)
        assertEquals("item_123", reel.items?.first()?.pk)
    }

    @Test
    fun `reelsMedia returns items from legacy standalone reels_media format`() {
        val json = """
            {
              "data": {
                "xdt_api__v1__feed__reels_media": {
                  "reels_media": [
                    {
                      "id": "user_456",
                      "items": [
                        {
                          "id": "item_456",
                          "pk": "item_456",
                          "media_type": 2
                        }
                      ]
                    }
                  ]
                }
              }
            }
        """.trimIndent()

        val response = gson.fromJson(json, UserStoryResponse::class.java)

        assertNotNull(response.data)
        assertEquals(1, response.reelsMedia.size)
        val reel = response.reelsMedia.first()
        assertEquals("user_456", reel.id)
        assertEquals(1, reel.items?.size)
        assertEquals("item_456", reel.items?.first()?.pk)
    }

    @Test
    fun `reelsMedia returns empty list when data is null or empty`() {
        val emptyResponse = UserStoryResponse()
        assertTrue(emptyResponse.reelsMedia.isEmpty())

        val emptyDataResponse = UserStoryResponse(data = UserStoryResponse.Data())
        assertTrue(emptyDataResponse.reelsMedia.isEmpty())
    }
}
