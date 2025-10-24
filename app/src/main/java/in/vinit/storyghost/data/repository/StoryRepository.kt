package `in`.vinit.storyghost.data.repository

import `in`.vinit.storyghost.data.model.SampleData

interface StoryRepository {

    suspend fun getData(): SampleData?
}