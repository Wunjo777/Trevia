package com.example.trevia.domain.schedule.model

import com.example.trevia.data.schedule.Event
import com.example.trevia.utils.toTimeString
import java.time.LocalTime

data class EventModel(
    val id: Long=0,
    val tripId: Long,
    val dayId: Long,
    val location: String,
    val address:String,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val description: String?
)

fun EventModel.toEvent(): Event
{
    return Event(
        id = this.id,
        tripId = this.tripId,
        dayId = this.dayId,
        location = this.location,
        address = this.address,
        startTime = this.startTime?.toTimeString(),
        endTime = this.endTime?.toTimeString(),
        description = this.description
    )
}
