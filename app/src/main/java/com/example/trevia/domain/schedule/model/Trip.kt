package com.example.trevia.domain.schedule.model

import java.time.LocalDate

data class TripModel(
    val id: Int,
    val name: String,
    val destination: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val days: List<DayModel>
)

fun TripModel.isValid(): Boolean
{
    return name.isNotBlank() &&
            destination.isNotBlank() &&
            startDate.isBefore(endDate) &&
            days.isNotEmpty()
}

