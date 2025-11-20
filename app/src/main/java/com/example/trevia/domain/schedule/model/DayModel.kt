package com.example.trevia.domain.schedule.model

import java.time.LocalDate

data class DayModel(
    val id: Int,
    val tripId: Int,
    val date: LocalDate,
    val indexInTrip: Int
)