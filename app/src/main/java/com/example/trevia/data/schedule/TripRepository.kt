package com.example.trevia.data.schedule

import com.example.trevia.domain.schedule.model.TripModel
import kotlinx.coroutines.flow.Flow

interface TripRepository
{
    suspend fun insertTrip(trip: Trip)

    suspend fun deleteTrip(trip: Trip)

    suspend fun updateTrip(trip: Trip)

    fun getAllTripsStream(): Flow<List<TripModel>>
}