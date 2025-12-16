package com.example.trevia.data.local.schedule

import com.example.trevia.data.remote.SyncState
import com.example.trevia.domain.schedule.model.TripModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

interface TripRepository
{
    suspend fun upsertTrip(tripModel: TripModel): Long

    suspend fun upsertTrips(tripModels: List<TripModel>)

    suspend fun deleteTripById(tripId: Long)

    suspend fun hardDeleteTripsByIds(tripIds: List<Long>)

    suspend fun updateTripWithLcObjectId(tripId: Long, lcObjectId: String)

    suspend fun getTripsBySyncState(states: List<SyncState>): List<TripModel>

     suspend fun getTripIdMapByObjectIds(lcObjectIds: List<String>): Map<String,Long>

     suspend fun updateTripsWithSynced(tripIds: List<Long>)

    suspend fun updateTripsWithUpdatedAt(tripIds: List<Long>, updatedAt: Long)

    fun getAllTripsStream(): Flow<List<TripModel>>

    fun getTripByIdStream(tripId: Long): Flow<TripModel?>
}