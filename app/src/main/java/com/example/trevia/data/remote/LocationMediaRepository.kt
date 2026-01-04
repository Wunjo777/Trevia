package com.example.trevia.data.remote

import android.util.Log
import kotlinx.serialization.builtins.serializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationMediaRepository @Inject constructor(private val api: PixabayApiService)
{
    private val apiKey = "42398314-bf13704898e99b3132169989f"

    /**
     * 获取搜索结果中的第一个视频 URL
     */
    suspend fun getFirstVideoUrl(keyword: String): VideoUrlResult?
    {
        return try
        {
            val first = api.searchVideos(apiKey, keyword).hits.firstOrNull()
            first?.videos?.let { VideoUrlResult(it.small.url, it.medium.url, it.large.url) }
        } catch (e: Exception)
        {
            Log.e("EEE","getFirstVideoUrl error: ${e.message}")
            null
        }
    }


    suspend fun getFirstNImageUrls(keyword: String, count: Int = 1): List<String>
    {
        return try
        {
            val response = api.searchImages(apiKey, keyword)
            response.hits.take(count).map { it.largeImageURL }
        } catch (e: Exception)
        {
            Log.e("EEE","getFirstNImageUrls error: ${e.message}")
            emptyList()
        }
    }
}

data class VideoUrlResult(
    val small: String?,
    val medium: String?,
    val large: String?
)