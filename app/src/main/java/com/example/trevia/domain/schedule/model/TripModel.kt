package com.example.trevia.domain.schedule.model

import com.example.trevia.data.schedule.Trip
import com.example.trevia.utils.isoLocalDateToStr
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class TripModel(
    val id: Long = -1,
    val name: String,
    val destination: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
)

fun TripModel.isValid(): Boolean
{
    return name.isNotBlank() &&
            destination.isNotBlank() &&
            !startDate.isBefore(LocalDate.now()) &&
            startDate.isBefore(endDate)
}

fun TripModel.toTrip(): Trip
{
    val startDateString = startDate.isoLocalDateToStr()
    val endDateString = endDate.isoLocalDateToStr()
    return Trip(
        name = name,
        destination = destination,
        startDate = startDateString,
        endDate = endDateString
    )
}

