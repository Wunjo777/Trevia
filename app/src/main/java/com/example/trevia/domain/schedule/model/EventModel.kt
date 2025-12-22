package com.example.trevia.domain.schedule.model

import cn.leancloud.LCObject
import com.example.trevia.data.local.schedule.Event
import com.example.trevia.data.remote.SyncState
import com.example.trevia.utils.toTimeString
import java.time.LocalTime

data class EventModel(
    val id: Long = 0,
    val dayId: Long,
    val location: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val description: String?,
    val lcObjectId: String? = null,
    val syncState: SyncState = SyncState.PENDING,
    val updatedAt: Long = 0
)

fun EventModel.toEvent(): Event
{
    return Event(
        id = this.id,
        dayId = this.dayId,
        location = this.location,
        address = this.address,
        latitude = this.latitude,
        longitude = this.longitude,
        startTime = this.startTime?.toTimeString(),
        endTime = this.endTime?.toTimeString(),
        description = this.description,
        lcObjectId = this.lcObjectId,
        syncState = this.syncState,
        updatedAt = this.updatedAt
    )
}

fun EventModel.toLcObject(dayObjectId: String): LCObject
{
    val eventLcObject = if (this.lcObjectId == null)
        LCObject("Event")
    else
        LCObject.createWithoutData("Event", this.lcObjectId)
    eventLcObject.put("day", LCObject.createWithoutData("Day", dayObjectId))
    eventLcObject.put("location", this.location)
    eventLcObject.put("address", this.address)
    eventLcObject.put("latitude", this.latitude)
    eventLcObject.put("longitude", this.longitude)
    eventLcObject.put("startTime", this.startTime?.toTimeString())
    eventLcObject.put("endTime", this.endTime?.toTimeString())
    eventLcObject.put("description", this.description)
    return eventLcObject
}
