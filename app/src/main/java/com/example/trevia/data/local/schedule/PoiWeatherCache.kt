package com.example.trevia.data.local.schedule

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "poi_weather_cache")
data class PoiWeatherCache(
    @PrimaryKey val poiId: String,
    val poiTel: String,
    val poiWebsite: String,
    val poiPostCode: String,
    val poiEmail: String,
    val poiAddress:String,
    val weather: String,
    val temperature: String,
    val windDirection: String,
    val windPower: String,
    val humidity: String,
    val reportTime: String,
    val lastUpdated: Long
)
