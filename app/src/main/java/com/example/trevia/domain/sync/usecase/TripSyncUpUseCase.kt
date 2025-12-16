package com.example.trevia.domain.sync.usecase

import android.util.Log
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
            val trips =
                tripRepository.getTripsBySyncState(listOf(SyncState.PENDING, SyncState.DELETED))
            if (trips.isEmpty())
            {
                Log.d("syncup", "TripUploadUseCase: no need to upload.")
                return
            }

            val upserts = trips.filter { it.syncState == SyncState.PENDING }
            val deletes = trips.filter { it.syncState == SyncState.DELETED }

            Log.d("syncup", "TripUploadUseCase: upload ${upserts.size} trips, delete ${deletes.size} trips.")
            val currentTimeStamp = System.currentTimeMillis()

            if (deletes.isNotEmpty())
            {
                Log.d("syncup", "TripUploadUseCase: soft delete ${deletes.size} trips.")
                syncRepository.softDeleteTrips(deletes,currentTimeStamp)
//                TODO("hard delete trips on LC after 7 days")
            }

            if (upserts.isNotEmpty())
            {
                val tripToLcObject = syncRepository.upsertTrips(upserts,currentTimeStamp)
                tripToLcObject.forEach { (tripId, lcObjectId) ->
                    tripRepository.updateTripWithLcObjectId(tripId, lcObjectId)
                }
                tripRepository.updateTripsWithSynced(upserts.map { it.id })
            }

            //update time stamp for all
            tripRepository.updateTripsWithUpdatedAt(trips.map { it.id },currentTimeStamp)

    }
}