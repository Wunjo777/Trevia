package com.example.trevia.data.schedule

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val largeImgPath: String,
    val thumbnailPath: String,
    val uploadedToServer: Boolean
)
