package com.example.trevia.domain.schedule.model

import com.example.trevia.data.schedule.Event

data class EventModel(
    val id: Long,
    val dayId: Long,
)

fun EventModel.toEvent(): Event
{
    return Event(
        id = this.id,
        dayId = this.dayId,
    )
}
