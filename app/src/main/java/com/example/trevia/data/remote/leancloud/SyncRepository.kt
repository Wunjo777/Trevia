package com.example.trevia.data.remote.leancloud

import android.util.Log
import com.example.trevia.domain.schedule.model.TripModel
import com.example.trevia.domain.schedule.model.toLcObject
import com.example.trevia.domain.schedule.model.toLcObjectUpdateIsDelete
import javax.inject.Inject
import javax.inject.Singleton
import java.util.Date
import cn.leancloud.LCObject
import com.example.trevia.data.remote.SyncState
import com.example.trevia.utils.strToIsoLocalDate

@Singleton
class SyncRepository @Inject constructor(private val service: LeanCloudService)
{
    suspend fun upsertTrips(tripModels: List<TripModel>): UploadResult
    {
        return service.upsertDatas(tripModels.map { Pair(it.id, it.toLcObject()) })
    }

    suspend fun softDeleteTrips(tripModels: List<TripModel>)
    {
        Log.d("syncup", "softDeleteTrips: soft delete ${tripModels.size} trips.")
        service.softDeleteDatas(tripModels.map { it.toLcObjectUpdateIsDelete() })
    }

    suspend fun getTripsAfter(timeStamp: Long): List<TripModel>
    {
        return service.getDatasAfter(Date(timeStamp), className = "Trip").map { it.toTripModel() }
    }

}

private fun LCObject.toTripModel(): TripModel
{
    return TripModel(
        name = getString("name"),
        destination = getString("destination"),
        startDate = getString("startDate").strToIsoLocalDate(),
        endDate = getString("endDate").strToIsoLocalDate(),
        lcObjectId = getString("objectId"),
        syncState = if (getBoolean("isDeleted")) SyncState.DELETED else SyncState.SYNCED,
        updatedAt = getDate("updatedAt").time,
    )
}
