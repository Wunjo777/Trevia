package com.example.trevia.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixabayVideoResponse(
    val total: Int,
    val totalHits: Int,
    val hits: List<VideoHit>
)

@Serializable
data class VideoHit(
    val id: Int,
    val pageURL: String,
    val type: String,
    val tags: String,
    val duration: Int,
    @SerialName("picture_id")
    val pictureId: String,
    val videos: VideoUrls
)

@Serializable
data class VideoUrls(
    val large: VideoFile,
    val medium: VideoFile,
    val small: VideoFile
)

@Serializable
data class VideoFile(
    val url: String,
    val width: Int,
    val height: Int
)
