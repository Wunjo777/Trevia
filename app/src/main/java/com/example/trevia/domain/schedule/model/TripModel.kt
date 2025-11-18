package com.example.trevia.domain.schedule.model

import com.example.trevia.data.schedule.Trip
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
            !startDate.isBefore(LocalDate.now()) &&
            startDate.isBefore(endDate)
//            && days.isNotEmpty()
}

fun TripModel.toTrip(): Trip
{
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    val startDateString = startDate.format(formatter)
    val endDateString = endDate.format(formatter)
    return Trip(
        id = id,
        name = name,
        destination = destination,
        startDate = startDateString,
        endDate = endDateString
    )
}

