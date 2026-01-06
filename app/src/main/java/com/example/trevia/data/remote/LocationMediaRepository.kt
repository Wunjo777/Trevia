package com.example.trevia.data.remote

import android.util.Log
import com.example.trevia.data.local.cache.ImgUrlCache
import com.example.trevia.data.local.cache.ImgUrlCacheDao
import com.example.trevia.data.local.cache.VideoUrlCache
import com.example.trevia.data.local.cache.VideoUrlCacheDao
import kotlinx.serialization.builtins.serializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationMediaRepository @Inject constructor(
    private val api: PixabayApiService,
    private val imgUrlCacheDao: ImgUrlCacheDao,
    private val videoUrlCacheDao: VideoUrlCacheDao
)
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
            Log.e("EEE", "getFirstVideoUrl error: ${e.message}")
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
            Log.e("EEE", "getFirstNImageUrls error: ${e.message}")
            emptyList()
        }
    }


    // ==================== 图片相关 ====================

    suspend fun getImgUrlsByPoi(poiId: String): List<ImgUrlCache> {
        return imgUrlCacheDao.getImgUrlsByPoi(poiId)
    }

    suspend fun upsertImgUrls(poiId: String, imgUrls: List<String>) {
        val now = System.currentTimeMillis()
        val entities = imgUrls.map { url ->
            ImgUrlCache(poiId = poiId, imgUrl = url, updatedAt = now)
        }
        imgUrlCacheDao.upsertImgUrls(entities)
    }

    suspend fun deleteExpiredImages(expireBefore: Long) {
        imgUrlCacheDao.deleteExpired(expireBefore)
    }

    // ==================== 视频相关 ====================

    suspend fun getVideoUrlByPoi(poiId: String): VideoUrlCache? {
        return videoUrlCacheDao.getVideoUrlByPoi(poiId)
    }

    suspend fun upsertVideoUrl(poiId: String, video: VideoUrlResult) {
        val now = System.currentTimeMillis()
        videoUrlCacheDao.upsertVideoUrl(
            VideoUrlCache(
                poiId = poiId,
                videoUrlSmall = video.small,
                videoUrlMedium = video.medium,
                videoUrlLarge = video.large,
                updatedAt = now
            )
        )
    }

    suspend fun deleteExpiredVideos(expireBefore: Long) {
        videoUrlCacheDao.deleteExpired(expireBefore)
    }
}

data class VideoUrlResult(
    val small: String?,
    val medium: String?,
    val large: String?
)