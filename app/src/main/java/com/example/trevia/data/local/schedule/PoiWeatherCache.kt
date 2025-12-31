package com.example.trevia.data.local.schedule

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "poi_cache")
data class PoiWeatherCache(
    @PrimaryKey val poiId: String,
    val poiTel: String,
    val poiWebsite: String,
    val poiPostCode: String,
    val poiEmail: String,
    val poiAddress:String,
)

@Entity(tableName = "weather_cache")
data class WeatherCache(
    @PrimaryKey val poiId: String,
    val weather: String,
    val temperature: String,
    val windDirection: String,
    val windPower: String,
    val humidity: String,
    val reportTime: String,
    val lastUpdated: Long
)
