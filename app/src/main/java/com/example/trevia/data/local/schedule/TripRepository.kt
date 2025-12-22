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

    suspend fun hardDeleteTripsByObjectIds(tripObjectIds: List<String>)

    suspend fun updateTripWithLcObjectId(tripId: Long, lcObjectId: String)

    suspend fun updateTripWithUpdatedAt(tripId: Long, updatedAt: Long)

    suspend fun getTripsBySyncState(states: List<SyncState>): List<TripModel>

    suspend fun getTripMapByObjectIds(lcObjectIds: List<String>): Map<String, TripModel>

    suspend fun getTripIdByLcObjectId(lcObjectId: String): Long?

    suspend fun getTripIdsByObjectIds(lcObjectIds: List<String>): List<Long>

    suspend fun updateTripsWithSynced(tripIds: List<Long>)

    suspend fun getTripsByIds(tripIds: List<Long>): List<TripModel>

    fun getAllTripsStream(): Flow<List<TripModel>>

    fun getTripByIdStream(tripId: Long): Flow<TripModel?>
}