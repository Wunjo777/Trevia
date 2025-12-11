package com.example.trevia.domain.schedule.model

import java.time.LocalDate

data class TripWithDaysAndEventsModel
    (
    val id: Long = 0,
    val name: String,
    val destination: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val daysWithEvents: List<DayWithEventsModel> = emptyList()
)