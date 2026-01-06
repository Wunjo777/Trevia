package com.example.trevia.data.local.cache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "img_url_cache")
data class ImgUrlCache(
    @PrimaryKey(autoGenerate = true)
    val id:Long=0,
    val poiId: String,
    val imgUrl: String,
    val updatedAt: Long
)
