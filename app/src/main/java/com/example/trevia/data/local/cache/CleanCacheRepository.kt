package com.example.trevia.data.local.cache

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CleanCacheRepository @Inject constructor(
    private val commentCacheDao: CommentCacheDao,
    private val poiCacheDao: PoiCacheDao,
    private val weatherCacheDao: WeatherCacheDao
) {

    /* ---------- Comment ---------- */

    suspend fun deleteExpiredComments(expireBefore: Long) {
        commentCacheDao.deleteExpired(expireBefore)
    }

    suspend fun getCommentCount(): Int {
        return commentCacheDao.count()
    }

    suspend fun deleteLeastUsedComments(count: Int) {
        if (count > 0) {
            commentCacheDao.deleteLeastRecentlyUsed(count)
        }
    }

    /* ---------- Poi ---------- */

    suspend fun deleteExpiredPois(expireBefore: Long) {
        poiCacheDao.deleteExpired(expireBefore)
    }

    suspend fun getPoiCount(): Int {
        return poiCacheDao.count()
    }

    suspend fun deleteLeastUsedPois(count: Int) {
        if (count > 0) {
            poiCacheDao.deleteLeastRecentlyUsed(count)
        }
    }

    /* ---------- Weather ---------- */

    suspend fun deleteExpiredWeather(expireBefore: Long) {
        weatherCacheDao.deleteExpired(expireBefore)
    }
}
