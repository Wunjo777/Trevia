package com.example.trevia.data.leancloud

import com.example.trevia.domain.schedule.model.TripModel
import com.example.trevia.domain.schedule.model.toLcObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(private val service: LeanCloudService)
{
    suspend fun uploadTrips(tripModels: List<TripModel>): List<String>
    {
        return service.uploadDatas(tripModels.map { it.toLcObject() })
    }
}