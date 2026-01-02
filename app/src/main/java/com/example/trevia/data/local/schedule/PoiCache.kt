package com.example.trevia.data.local.schedule

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "poi_cache")
data class PoiCache(
    @PrimaryKey val poiId: String,
    val poiCityName: String,
    val poiTel: String,
    val poiWebsite: String,
    val poiPostCode: String,
    val poiEmail: String,
    val poiAddress:String,
    val updatedAt:Long
)


