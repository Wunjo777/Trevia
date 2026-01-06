package com.example.trevia.data.local.cache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_url_cache")
data class VideoUrlCache(
    @PrimaryKey val poiId: String,         // POI 唯一 ID
    val videoUrlSmall: String?,            // 可选：小清晰度
    val videoUrlMedium: String?,
    val videoUrlLarge: String?,
    val updatedAt: Long
)
