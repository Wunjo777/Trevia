package com.example.trevia.data.local.schedule

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PoiWeatherCacheDao
{

    @Query("SELECT * FROM poi_weather_cache WHERE poiId = :poiId")
    suspend fun getPoiWeather(poiId: String): PoiWeatherCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoiWeather(entity: PoiWeatherCache)
}
