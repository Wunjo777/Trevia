package com.example.trevia.domain.schedule.model

import java.time.LocalDate

data class DayWithEventsModel(
    val id: Long = 0,
    val tripId: Long,
    val date: LocalDate,
    val indexInTrip: Int,
    val events: List<EventModel> = emptyList()
)
