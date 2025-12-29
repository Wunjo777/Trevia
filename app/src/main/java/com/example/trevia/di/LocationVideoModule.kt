package com.example.trevia.di

import android.content.Context
import com.example.trevia.data.remote.PixabayApiService
import com.example.trevia.data.remote.amap.AMapService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import okhttp3.MediaType.Companion.toMediaType
import kotlinx.serialization.json.Json

@Module
@InstallIn(SingletonComponent::class)
object LocationVideoModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://pixabay.com/")
        .addConverterFactory(Json { ignoreUnknownKeys = true }.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun providePixabayApi(retrofit: Retrofit): PixabayApiService =
        retrofit.create(PixabayApiService::class.java)
}
