package com.example.trevia.data.local.cache

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface VideoUrlCacheDao
{

    // 获取某个 POI 的视频 URL
    @Query("SELECT * FROM video_url_cache WHERE poiId = :poiId")
    suspend fun getVideoUrlByPoi(poiId: String): VideoUrlCache?

    @Upsert
    suspend fun upsertVideoUrl(entity: VideoUrlCache)

    // 删除过期视频缓存
    @Query("DELETE FROM video_url_cache WHERE updatedAt < :expireBefore")
    suspend fun deleteExpired(expireBefore: Long)
}
