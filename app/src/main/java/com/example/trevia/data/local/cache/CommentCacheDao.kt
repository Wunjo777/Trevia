package com.example.trevia.data.local.cache

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface CommentCacheDao
{
    @Query("SELECT * FROM comment_cache WHERE poiId = :poiId")
    suspend fun getCommentsCache(poiId: String): List<CommentCache>

    @Upsert
    suspend fun upsertCommentsCache(entity: List<CommentCache>)

    @Query("UPDATE comment_cache SET lastAccess = :lastAccess WHERE poiId = :poiId")
    suspend fun updateCommentsLastAccess(poiId: String, lastAccess: Long)

    @Query("DELETE FROM comment_cache WHERE updatedAt < :expireBefore")
    suspend fun deleteExpired(expireBefore: Long)

    @Query("""
        DELETE FROM comment_cache 
        WHERE poiId IN (
            SELECT poiId FROM comment_cache
            ORDER BY lastAccess ASC
            LIMIT :count
        )
    """)
    suspend fun deleteLeastRecentlyUsed(count: Int)

    @Query("SELECT COUNT(*) FROM comment_cache")
    suspend fun count(): Int
}