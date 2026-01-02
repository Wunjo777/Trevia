package com.example.trevia.data.local.schedule

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherCacheDao
{
    @Query("SELECT * FROM weather_cache WHERE poiId = :poiId")
    suspend fun getWeatherCache(poiId: String): WeatherCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherCache(entity: WeatherCache)
}