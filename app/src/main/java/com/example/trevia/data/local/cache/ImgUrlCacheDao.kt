package com.example.trevia.data.local.cache

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ImgUrlCacheDao {

    // 获取某个 POI 的所有图片 URL
    @Query("SELECT * FROM img_url_cache WHERE poiId = :poiId")
    suspend fun getImgUrlsByPoi(poiId: String): List<ImgUrlCache>

    @Upsert
    suspend fun upsertImgUrls(entity: List<ImgUrlCache>)

    // 删除过期图片缓存
    @Query("DELETE FROM img_url_cache WHERE updatedAt < :expireBefore")
    suspend fun deleteExpired(expireBefore: Long)
}