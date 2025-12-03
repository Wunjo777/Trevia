package com.example.trevia.data.schedule

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.trevia.domain.imgupload.model.PhotoModel

@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val largeImgPath: String,
    val thumbnailPath: String,
    val uploadedToServer: Boolean
)

fun Photo.toPhotoModel(): PhotoModel = PhotoModel(
    id = id,
    largeImgPath = largeImgPath,
    thumbnailPath = thumbnailPath,
    uploadedToServer = uploadedToServer
)
