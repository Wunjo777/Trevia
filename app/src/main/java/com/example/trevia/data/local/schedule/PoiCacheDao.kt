package com.example.trevia.data.local.schedule

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PoiCacheDao
{

    @Query("SELECT * FROM poi_cache WHERE poiId = :poiId")
    suspend fun getPoiCache(poiId: String): PoiCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoiCache(entity: PoiCache)
}
