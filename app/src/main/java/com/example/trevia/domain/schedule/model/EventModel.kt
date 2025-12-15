package com.example.trevia.domain.schedule.model

import com.example.trevia.data.local.schedule.Event
import com.example.trevia.utils.toTimeString
import java.time.LocalTime

data class EventModel(
    val id: Long=0,
    val dayId: Long,
    val location: String,
    val address:String,
    val latitude: Double,
    val longitude: Double,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val description: String?
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
        description = this.description
    )
}
