package com.example.trevia.domain.schedule.model

import com.example.trevia.data.schedule.Event

data class EventModel(
    val id: Long,
    val tripId: Long,
    val dayId: Long,
    val location: String,
    val startTime: String,
    val endTime: String,
    val description: String = ""
)

fun EventModel.toEvent(): Event
{
    return Event(
        id = this.id,
        tripId = this.tripId,
        dayId = this.dayId,
        location = this.location,
        startTime = this.startTime,
        endTime = this.endTime,
        description = this.description
    )
}
