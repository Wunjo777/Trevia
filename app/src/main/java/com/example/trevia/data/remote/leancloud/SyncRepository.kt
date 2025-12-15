package com.example.trevia.data.remote.leancloud

import com.example.trevia.domain.schedule.model.TripModel
import com.example.trevia.domain.schedule.model.toLcObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(private val service: LeanCloudService)
{
    suspend fun upsertTrips(tripModels: List<TripModel>): Map<Long, String>
    {
        return service.upsertDatas(tripModels.map { Pair(it.id, it.toLcObject()) })
    }

    suspend fun deleteTrips(tripModels: List<TripModel>)
    {
        service.deleteDatas(tripModels.map { it.toLcObject() })
    }
}