package com.example.trevia.domain.sync.usecase

import com.example.trevia.data.remote.leancloud.SyncRepository
import com.example.trevia.data.local.schedule.TripRepository
import com.example.trevia.data.remote.SyncState
import com.example.trevia.di.OfflineRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripSyncUpUseCase @Inject constructor(
    @OfflineRepo private val tripRepository: TripRepository,
    private val syncRepository: SyncRepository
)
{
    suspend operator fun invoke()
    {
        try
        {
            val trips =
                tripRepository.getTripsBySyncState(listOf(SyncState.PENDING, SyncState.DELETED))
            if (trips.isEmpty())
            {
//                Log.d("test", "TripUploadUseCase: no need to upload.")
                return
            }

            val upserts = trips.filter { it.syncState == SyncState.PENDING }
            val deletes = trips.filter { it.syncState == SyncState.DELETED }

            if (deletes.isNotEmpty())
            {
                syncRepository.deleteTrips(deletes)
                tripRepository.hardDeleteTrips(deletes)
            }

            if (upserts.isNotEmpty())
            {
                val tripToLcObject = syncRepository.upsertTrips(upserts)
                tripToLcObject.forEach { (tripId, lcObjectId) ->
                    tripRepository.updateTripWithLcObjectId(tripId, lcObjectId)
                }
                tripRepository.updateTripsWithSynced(upserts.map { it.id })
            }
        } catch (e: Exception)
        {
            e.printStackTrace()
        }

    }
}