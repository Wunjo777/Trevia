package com.example.trevia.utils

import com.example.trevia.data.local.cache.CleanCacheRepository
import javax.inject.Inject
import javax.inject.Singleton

class CacheCleaner @Inject constructor(
    private val repository: CleanCacheRepository
) {

    suspend fun clean(
        poiExpireDurationMs: Long,
        commentExpireDurationMs: Long,
        weatherExpireDurationMs: Long,
        maxCommentSize: Int,
        maxPoiSize: Int,
    ) {
        val now = System.currentTimeMillis()
        val commentExpireBefore = now - commentExpireDurationMs
        val poiExpireBefore = now - poiExpireDurationMs
        val weatherExpireBefore = now - weatherExpireDurationMs

        // 1️⃣ 先清理过期缓存（强规则）
        repository.deleteExpiredComments(commentExpireBefore)
        repository.deleteExpiredPois(poiExpireBefore)
        repository.deleteExpiredWeather(weatherExpireBefore)

        // 2️⃣ 再按 LRU 清理超量缓存（兜底规则）
        cleanBySize(
            repository.getCommentCount(),
            maxCommentSize
        ) { excess ->
            repository.deleteLeastUsedComments(excess)
        }

        cleanBySize(
            repository.getPoiCount(),
            maxPoiSize
        ) { excess ->
            repository.deleteLeastUsedPois(excess)
        }
    }

    private suspend fun cleanBySize(
        currentCount: Int,
        maxSize: Int,
        deleteAction: suspend (excess: Int) -> Unit
    ) {
        if (currentCount > maxSize) {
            deleteAction(currentCount - maxSize)
        }
    }
}
