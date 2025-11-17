package com.example.trevia.di

import com.example.trevia.data.schedule.OfflineTripRepository
import com.example.trevia.data.schedule.TripRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule
{
    @Binds
    @OfflineRepo
    abstract fun bindOfflineTripRepository(impl: OfflineTripRepository): TripRepository
}