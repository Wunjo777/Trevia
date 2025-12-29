package com.example.trevia.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class PixabayImageResponse(
    val total: Int,
    val totalHits: Int,
    val hits: List<ImageHit>
)

@Serializable
data class ImageHit(
    val id: Int,
    val pageURL: String,
    val tags: String,
    val largeImageURL: String,
    val previewURL: String,
    val webformatURL: String,
)
