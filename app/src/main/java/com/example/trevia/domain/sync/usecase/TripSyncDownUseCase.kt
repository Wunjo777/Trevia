package com.example.trevia.domain.sync.usecase

import android.util.Log
import com.example.trevia.data.local.SyncDatastoreRepository
import com.example.trevia.data.local.schedule.TripRepository
import com.example.trevia.data.remote.SyncState
import com.example.trevia.data.remote.leancloud.SyncRepository
import com.example.trevia.di.OfflineRepo
import com.example.trevia.ui.user.LoginScreen
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripSyncDownUseCase @Inject constructor(
    @OfflineRepo private val tripRepository: TripRepository,
    private val syncRepository: SyncRepository,
    private val syncDatastoreRepository: SyncDatastoreRepository
)
{
    suspend operator fun invoke()
    {
        val lastSyncTime = syncDatastoreRepository.getLastSyncTime()
        Log.d("test", "lastSyncTime: $lastSyncTime")
        val changedTrips = syncRepository.getTripsAfter(lastSyncTime)
        Log.d("test", "changedTripsSize: ${changedTrips.size}")
        if (changedTrips.isEmpty())
        {
            return
        }

        val upserts = changedTrips.filter { it.syncState == SyncState.SYNCED }
        val deletes = changedTrips.filter { it.syncState == SyncState.DELETED }
        Log.d("test", "upsertsSize: ${upserts.size}")
        Log.d("test", "deletesSize: ${deletes.size}")

        val deletesTripIdMap =
            tripRepository.getTripIdMapByObjectIds(deletes.map { it.lcObjectId!! })
        tripRepository.hardDeleteTripsByIds(deletesTripIdMap.values.toList())

        val upsertsTripIdMap =
            tripRepository.getTripIdMapByObjectIds(upserts.map { it.lcObjectId!! })
        val tripsToUpsert = upserts.map {
            val tripId = upsertsTripIdMap[it.lcObjectId!!]
            if (tripId != null) it.copy(id = tripId) else it
        }

        tripRepository.upsertTrips(tripsToUpsert)

        syncDatastoreRepository.setLastSyncTime(changedTrips.last().updatedAt)
        Log.d("test", "updatedSyncTime: ${Date(syncDatastoreRepository.getLastSyncTime())}")
    }
}