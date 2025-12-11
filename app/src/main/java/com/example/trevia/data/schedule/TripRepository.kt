package com.example.trevia.data.schedule

import com.example.trevia.domain.schedule.model.TripModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

interface TripRepository
{
    suspend fun insertTrip(tripModel: TripModel): Long

    suspend fun deleteTrip(tripModel: TripModel)

    suspend fun updateTrip(tripModel: TripModel)

    suspend fun deleteTripById(tripId: Long)

    suspend fun getTripsWithoutLcObjectId(): List<TripModel>

    suspend fun updateTripsWithLcObjectId(tripModels: List<TripModel>, lcObjectIds: List<String>)

    fun getAllTripsStream(): Flow<List<TripModel>>

    fun getTripByIdStream(tripId: Long): Flow<TripModel?>
}