package com.example.trevia.data.remote.leancloud

import android.util.Log
import cn.leancloud.LCObject
import com.example.trevia.data.remote.SyncState
import com.example.trevia.data.remote.toLcObjectUpdateIsDelete
import com.example.trevia.domain.schedule.model.TripModel
import com.example.trevia.domain.schedule.model.toLcObject
import com.example.trevia.utils.strToIsoLocalDate
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncTripRepository @Inject constructor(private val service: LeanCloudService)
{
    suspend fun upsertTrips(tripModels: List<TripModel>): UploadResult
    {
        return service.upsertDatas(tripModels.map { Pair(it.id, it.toLcObject()) })
    }

    suspend fun softDeleteTrips(tripModels: List<TripModel>): Map<Long, String>
    {
        val responses = service.softDeleteDatas(tripModels.map {
            toLcObjectUpdateIsDelete(
                "Trip",
                it.lcObjectId
            )
        })

        val idMap = mutableMapOf<Long, String>()

        // responses 与 tripModels 一一对应
        tripModels.indices
            .filter { tripModels[it].lcObjectId == null } // 筛选出 lcObjectId 为 null 的元素
            .forEach { index ->
                val success = responses.getJSONObject(index)
                val lcObjectId = success.getString("objectId")
                idMap[tripModels[index].id] = lcObjectId
            }

        return idMap
    }

    suspend fun getTripsAfter(timeStamp: Long): List<TripModel>
    {
        return service.getDatasAfter(Date(timeStamp), className = "Trip").map { it.toTripModel() }
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
}


