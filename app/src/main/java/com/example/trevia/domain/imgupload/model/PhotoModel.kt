package com.example.trevia.domain.imgupload.model

import com.example.trevia.data.schedule.Photo

data class PhotoModel(
    val id: Long = 0,
    val largeImgPath: String="",
    val thumbnailPath: String,
    val uploadedToServer: Boolean
)

fun PhotoModel.toPhoto() = Photo(
    id = this.id,
    largeImgPath = this.largeImgPath,
    thumbnailPath = this.thumbnailPath,
    uploadedToServer = this.uploadedToServer
)
