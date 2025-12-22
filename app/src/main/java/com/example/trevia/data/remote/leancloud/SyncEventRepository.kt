package com.example.trevia.data.remote.leancloud

import cn.leancloud.LCObject
import com.example.trevia.data.local.schedule.DayRepository
import com.example.trevia.data.local.schedule.EventRepository
import com.example.trevia.data.remote.SyncState
import com.example.trevia.data.remote.toLcObjectUpdateIsDelete
import com.example.trevia.domain.schedule.model.EventModel
import com.example.trevia.domain.schedule.model.toLcObject
import com.example.trevia.utils.toLocalTime
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncEventRepository @Inject constructor(
    private val service: LeanCloudService,
    private val dayRepository: DayRepository,
)
{
    suspend fun upsertEvents(dayObjectIdToEventModel: List<Pair<String, EventModel>>): UploadResult
    {
        return service.upsertDatas(dayObjectIdToEventModel.map { (dayObjectId, eventModel) ->
            Pair(
                eventModel.id,
                eventModel.toLcObject(dayObjectId)
            )
        })
    }
    suspend fun softDeleteEvents(eventModels: List<EventModel>): Map<Long, String>
    {
        val responses = service.softDeleteDatas(eventModels.map {
            toLcObjectUpdateIsDelete(
                "Event",
                it.lcObjectId
            )
        })
        val idMap = mutableMapOf<Long, String>()

        // responses 与 eventModels 一一对应
        //第一次上传的event，获取返回的lcObjectId
        eventModels.indices
            .filter { eventModels[it].lcObjectId == null } // 筛选出 lcObjectId 为 null 的元素
            .forEach { index ->
                val success = responses.getJSONObject(index)
                val lcObjectId = success.getString("objectId")
                idMap[eventModels[index].id] = lcObjectId
            }

        return idMap
    }

    suspend fun getEventsAfter(lastSyncTime: Long): List<EventModel>
    {
        return service.getDatasAfter(Date(lastSyncTime), "Event").map { it.toEventModel() }
    }

    private suspend fun LCObject.toEventModel(): EventModel
    {
        val isDeleted = this.getBoolean("isDeleted")
        return EventModel(
            dayId = if (!isDeleted)
            {
                val dayObjectId = this.getLCObject<LCObject>("day").objectId
                dayRepository.getDayIdByLcObjectId(dayObjectId)!!
            }
            else -1,
            location = this.getString("location"),
            address = this.getString("address"),
            latitude = this.getDouble("latitude"),
            longitude = this.getDouble("longitude"),
            startTime = this.getString("startTime")?.toLocalTime(),
            endTime = this.getString("endTime")?.toLocalTime(),
            description = this.getString("description"),
            lcObjectId = this.objectId,
            syncState = if (isDeleted) SyncState.DELETED else SyncState.SYNCED,
            updatedAt = this.getDate("updatedAt").time,
        )
    }
}