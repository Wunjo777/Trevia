package com.example.trevia.data.schedule

import javax.inject.Inject

class OfflineTripRepository @Inject constructor(
    private val tripDao: TripDao
) : TripRepository
{
    override suspend fun insertTrip(trip: Trip) = tripDao.insert(trip)

    override suspend fun deleteTrip(trip: Trip) = tripDao.delete(trip)

    override suspend fun updateTrip(trip: Trip) = tripDao.update(trip)
}