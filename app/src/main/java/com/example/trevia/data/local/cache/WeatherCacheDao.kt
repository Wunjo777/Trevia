package com.example.trevia.data.local.cache

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface WeatherCacheDao
{
    @Query("SELECT * FROM weather_cache WHERE poiId = :poiId")
    suspend fun getWeatherCache(poiId: String): WeatherCache?

    @Upsert
    suspend fun upsertWeatherCache(entity: WeatherCache)

    @Query("DELETE FROM weather_cache WHERE updatedAt < :expireBefore")
    suspend fun deleteExpired(expireBefore: Long)
}