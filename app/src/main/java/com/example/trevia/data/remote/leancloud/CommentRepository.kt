package com.example.trevia.data.remote.leancloud

import com.example.trevia.data.local.cache.CachePolicy.COMMENT_CACHE_TIMEOUT_MS
import com.example.trevia.data.local.cache.CommentCache
import com.example.trevia.data.local.cache.CommentCacheDao
import com.example.trevia.domain.location.model.CommentModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(
    private val commentCacheDao: CommentCacheDao,
)
{

    suspend fun getCachedComments(poiId: String): List<CommentModel>?
    {
        val now = System.currentTimeMillis()

        val cachedList = commentCacheDao.getCommentsCache(poiId)

        // 1️⃣ 没缓存
        if (cachedList.isEmpty()) return null

        // 2️⃣ 取任意一条检查缓存是否过期（通常取第一条）
        val updatedAt = cachedList.first().updatedAt
        if (now - updatedAt > COMMENT_CACHE_TIMEOUT_MS) return null

        // 3️⃣ 转成领域模型
        return cachedList.map {
            CommentModel(
                content = it.commentContent
            )
        }
    }

    suspend fun upsertCommentsCache(
        poiId: String,
        comments: List<CommentModel>
    )
    {
        val now = System.currentTimeMillis()
       val commentCaches = comments.map {
           CommentCache(
               poiId = poiId,
               commentContent = it.content,
               updatedAt = now,
               lastAccess = now
           )
        }
        commentCacheDao.upsertCommentsCache(commentCaches)
    }

    suspend fun updateCommentsLastAccess(
        poiId: String
    )
    {
        val now = System.currentTimeMillis()
        commentCacheDao.updateCommentsLastAccess(poiId, now)
    }
}