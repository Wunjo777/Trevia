package com.example.trevia.domain.sync.usecase

import android.util.Log
import com.example.trevia.data.local.schedule.TripRepository
import com.example.trevia.data.remote.SyncState
import com.example.trevia.data.remote.leancloud.SyncTripRepository
import com.example.trevia.di.OfflineRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripSyncUpUseCase @Inject constructor(
    @OfflineRepo private val tripRepository: TripRepository,
    private val syncTripRepository: SyncTripRepository
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

        Log.d(
            "syncup",
            "TripUploadUseCase: upload ${upserts.size} trips, delete ${deletes.size} trips."
        )
        val lcObjectIdUpdates = mutableMapOf<Long, String>()

        if (deletes.isNotEmpty())
        {
            Log.d("syncup", "TripUploadUseCase: soft delete ${deletes.size} trips.")
            val idMap = syncTripRepository.softDeleteTrips(deletes)
            lcObjectIdUpdates.putAll(idMap)//在未上传到服务器之前就将本地的数据删除会产生问题，因此删除时也应获取objectId更新本地
//                TODO("hard delete trips on LC after 7 days")
        }

        if (upserts.isNotEmpty())
        {
            val uploadResult = syncTripRepository.upsertTrips(upserts)
            val idMap = uploadResult.dataIdToLcObjectId
            val updatedAtList = uploadResult.updatedAtList

            lcObjectIdUpdates.putAll(idMap)

            updatedAtList.forEachIndexed { index, updatedAt ->
                tripRepository.updateTripWithUpdatedAt(upserts[index].id, updatedAt)
            }
        }

        if (lcObjectIdUpdates.isNotEmpty())
        {
            lcObjectIdUpdates.forEach { (tripId, lcObjectId) ->
                tripRepository.updateTripWithLcObjectId(tripId, lcObjectId)
            }
        }
        //所有数据上传完成后，将本地数据标记为已上传
        tripRepository.updateTripsWithSynced(trips.map { it.id })
    }
}