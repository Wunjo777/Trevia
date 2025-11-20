package com.example.trevia.data.schedule

import com.example.trevia.domain.schedule.model.TripModel
import kotlinx.coroutines.flow.Flow

interface TripRepository
{
    suspend fun insertTrip(trip: Trip): Long

    suspend fun deleteTrip(trip: Trip)

    suspend fun updateTrip(trip: Trip)

    suspend fun deleteTripById(tripId: Long)

    fun getAllTripsStream(): Flow<List<TripModel>>

    fun getTripByIdStream(tripId: Long): Flow<TripModel?>
}