package com.example.trevia.di

import android.content.Context
import com.example.trevia.data.amap.AMapService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule
{
    @Provides
    @Singleton
    fun provideAMapService(@ApplicationContext context: Context): AMapService
    {
        return AMapService(context)  // 返回 AMapService 实例
    }
}