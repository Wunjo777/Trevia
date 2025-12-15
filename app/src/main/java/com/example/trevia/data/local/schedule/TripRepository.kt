package com.example.trevia.data.local.schedule

import com.example.trevia.data.remote.SyncState
import com.example.trevia.domain.schedule.model.TripModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

interface TripRepository
{
    suspend fun insertTrip(tripModel: TripModel): Long

    suspend fun updateTrip(tripModel: TripModel)

    suspend fun deleteTripById(tripId: Long)

    suspend fun hardDeleteTrips(tripModels: List<TripModel>)

    suspend fun updateTripWithLcObjectId(tripId: Long, lcObjectId: String)

    suspend fun getTripsBySyncState(states: List<SyncState>): List<TripModel>

     suspend fun updateTripsWithSynced(tripIds: List<Long>)


    fun getAllTripsStream(): Flow<List<TripModel>>

    fun getTripByIdStream(tripId: Long): Flow<TripModel?>
}