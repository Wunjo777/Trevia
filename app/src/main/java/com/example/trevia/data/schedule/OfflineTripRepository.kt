package com.example.trevia.data.schedule

import com.example.trevia.domain.schedule.model.TripModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineTripRepository @Inject constructor(
    private val tripDao: TripDao
) : TripRepository
{
    override suspend fun insertTrip(trip: Trip) = tripDao.insert(trip)

    override suspend fun deleteTrip(trip: Trip) = tripDao.delete(trip)

    override suspend fun updateTrip(trip: Trip) = tripDao.update(trip)

    override suspend fun deleteTripById(tripId: Long) = tripDao.deleteTripById(tripId)

    override fun getAllTripsStream(): Flow<List<TripModel>>
    {
        return tripDao.getAllTrips().map { trips ->
            trips.map { it.toTripModel() }
        }
    }
}