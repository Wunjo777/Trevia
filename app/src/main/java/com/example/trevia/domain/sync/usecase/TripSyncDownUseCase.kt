package com.example.trevia.domain.sync.usecase

import android.util.Log
import com.example.trevia.data.local.SyncDatastoreRepository
import com.example.trevia.data.local.schedule.TripRepository
import com.example.trevia.data.remote.SyncState
import com.example.trevia.data.remote.leancloud.SyncRepository
import com.example.trevia.di.OfflineRepo
import com.example.trevia.domain.schedule.model.TripModel
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

        val deletesTripIds =
            tripRepository.getTripIdsByObjectIds(deletes.map { it.lcObjectId!! })
        if (deletesTripIds.isNotEmpty())
        {
            tripRepository.hardDeleteTripsByIds(deletesTripIds)
        }

        val tripsToArchive = mutableListOf<TripModel>()

        val upsertsTripMap =
            tripRepository.getTripMapByObjectIds(upserts.map { it.lcObjectId!! })

        val tripsToUpsert = upserts.mapNotNull { trip ->
            val currentTrip = upsertsTripMap[trip.lcObjectId!!]
            when {
                currentTrip == null -> trip//服务端新的，直接插入
                currentTrip.updatedAt >= trip.updatedAt || currentTrip.syncState == SyncState.DELETED -> null//刚上传和本地删除的，不更新
                else -> {//需要更新
                    if (currentTrip.syncState == SyncState.PENDING) {//本地存在冲突，先归档
                        tripsToArchive.add(currentTrip)
                    }
                    trip.copy(id = currentTrip.id)
                }
            }
        }

        Log.d("test", "tripsToUpsertSize: ${tripsToUpsert.size}")
        Log.d("test", "tripsToArchiveSize: ${tripsToArchive.size}")

        if(tripsToUpsert.isNotEmpty())
        {
            tripRepository.upsertTrips(tripsToUpsert)
        }

        if(tripsToArchive.isNotEmpty())
        {
            TODO("archiveTrips")
        }

        syncDatastoreRepository.setLastSyncTime(changedTrips.last().updatedAt)
        Log.d("test", "updatedSyncTime: ${Date(syncDatastoreRepository.getLastSyncTime())}")
    }
}