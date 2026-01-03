package com.example.trevia.data.local.cache

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface PoiCacheDao
{

    @Query("SELECT * FROM poi_cache WHERE poiId = :poiId")
    suspend fun getPoiCache(poiId: String): PoiCache?

    @Upsert
    suspend fun upsertPoiCache(entity: PoiCache)

    @Query("UPDATE poi_cache SET lastAccess = :lastAccess WHERE poiId = :poiId")
    suspend fun updatePoiCacheLastAccess(poiId: String, lastAccess: Long)

    @Query("DELETE FROM poi_cache WHERE updatedAt < :expireBefore")
    suspend fun deleteExpired(expireBefore: Long)

    @Query("""
        DELETE FROM poi_cache 
        WHERE poiId IN (
            SELECT poiId FROM poi_cache
            ORDER BY lastAccess ASC
            LIMIT :count
        )
    """)
    suspend fun deleteLeastRecentlyUsed(count: Int)

    @Query("SELECT COUNT(*) FROM poi_cache")
    suspend fun count(): Int
}
