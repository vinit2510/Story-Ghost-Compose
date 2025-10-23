package `in`.vinit.storyghost.data.repository

import `in`.vinit.storyghost.data.model.SampleData
import `in`.vinit.storyghost.data.remote.StoryAPI

class StoryRepositoryImpl(private val api: StoryAPI) : StoryRepository {

    override suspend fun getData(): SampleData? {
        return api.getData()
    }
}