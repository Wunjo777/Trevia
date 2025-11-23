package com.example.trevia.data.schedule

import com.example.trevia.domain.schedule.model.TripModel
import com.example.trevia.domain.schedule.model.toTrip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineTripRepository @Inject constructor(
    private val tripDao: TripDao
) : TripRepository
{
    override suspend fun insertTrip(tripModel: TripModel): Long = tripDao.insert(tripModel.toTrip())


    override suspend fun deleteTrip(tripModel: TripModel) = tripDao.delete(tripModel.toTrip())

    override suspend fun updateTrip(tripModel: TripModel) = tripDao.update(tripModel.toTrip())

    override suspend fun deleteTripById(tripId: Long) = tripDao.deleteTripById(tripId)

    override fun getTripByIdStream(tripId: Long): Flow<TripModel?>
    {
        return tripDao.getTripById(tripId).map { it?.toTripModel() }
    }

    override fun getAllTripsStream(): Flow<List<TripModel>>
    {
        return tripDao.getAllTrips().map { trips ->
            trips.map { it.toTripModel() }
        }
    }
}