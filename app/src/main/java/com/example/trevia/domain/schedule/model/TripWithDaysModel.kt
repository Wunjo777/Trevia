package com.example.trevia.domain.schedule.model

import java.time.LocalDate

data class TripWithDaysModel
    (
    val tripId: Long = -1,
    val name: String,
    val destination: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
)