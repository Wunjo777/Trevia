package com.example.trevia.data.local.cache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherCache(
    @PrimaryKey val poiId: String,
    val weather: String,
    val temperature: String,
    val windDirection: String,
    val windPower: String,
    val humidity: String,
    val updatedAt: Long
)