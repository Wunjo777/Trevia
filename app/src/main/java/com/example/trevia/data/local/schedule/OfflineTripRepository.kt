package com.example.trevia.data.local.schedule

import com.example.trevia.data.remote.SyncState
import com.example.trevia.domain.schedule.model.TripModel
import com.example.trevia.domain.schedule.model.toTrip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

import javax.inject.Singleton

@Singleton
class OfflineTripRepository @Inject constructor(
    private val tripDao: TripDao
) : TripRepository
{
    override suspend fun upsertTrip(tripModel: TripModel): Long = tripDao.upsert(tripModel.toTrip())

    override suspend fun upsertTrips(tripModels: List<TripModel>) =
        tripDao.upsertTrips(tripModels.map { it.toTrip() })

    override suspend fun deleteTripById(tripId: Long) = tripDao.softDeleteTripById(tripId)

    override suspend fun hardDeleteTripsByIds(tripIds: List<Long>) =
        tripDao.hardDeleteTripsByIds(tripIds)

    override suspend fun getTripsBySyncState(states: List<SyncState>): List<TripModel> =
        tripDao.getTripsBySyncState(states).map { it.toTripModel() }

    override suspend fun getTripIdMapByObjectIds(lcObjectIds: List<String>): Map<String, Long> =
        tripDao.getTripsByObjectIds(lcObjectIds).associate { it.lcObjectId!! to it.id }


    override suspend fun updateTripsWithSynced(tripIds: List<Long>) =
        tripDao.updateTripsWithSynced(tripIds)

    override suspend fun updateTripsWithUpdatedAt(tripIds: List<Long>, updatedAt: Long) =
        tripDao.updateTripsWithUpdatedAt(tripIds, updatedAt)



    override suspend fun updateTripWithLcObjectId(tripId: Long, lcObjectId: String)
    {
        tripDao.updateTripWithLcObjectId(tripId, lcObjectId)
    }

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