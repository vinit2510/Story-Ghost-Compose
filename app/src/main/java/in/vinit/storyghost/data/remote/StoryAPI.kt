package `in`.vinit.storyghost.data.remote

import `in`.vinit.storyghost.data.model.SampleData
import retrofit2.http.GET

interface StoryAPI {

    @GET("todos/1")
    suspend fun getData(): SampleData?

}