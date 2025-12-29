package com.example.trevia.data.remote

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationVideoRepository @Inject constructor(private val api: PixabayApiService)
{
    private val apiKey = "42398314-bf13704898e99b3132169989f"

    /**
     * 获取搜索结果中的第一个视频 URL
     */
    suspend fun getFirstVideoUrl(keyword: String): String? {
        return try {
            val response = api.searchVideos(apiKey, keyword)
            response.hits.firstOrNull()?.videos?.medium?.url
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getFirstImageUrl(keyword: String): String? {
        return try {
            val response = api.searchImages(apiKey, keyword)
            response.hits.firstOrNull()?.largeImageURL
        } catch (e: Exception) {
            null
        }
    }
}