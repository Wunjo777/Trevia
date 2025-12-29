package com.example.trevia.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayApiService {

    @GET("api/videos/")
    suspend fun searchVideos(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("per_page") perPage: Int = 5
    ):PixabayVideoResponse

    @GET("api/")
    suspend fun searchImages(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("per_page") perPage: Int = 5
    ): PixabayImageResponse
}
