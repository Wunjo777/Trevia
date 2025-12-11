package com.example.trevia.domain.schedule.model

import cn.leancloud.LCObject
import com.example.trevia.data.schedule.Trip
import com.example.trevia.utils.isoLocalDateToStr
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class TripModel(
    val id: Long = 0,
    val name: String,
    val destination: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val lcObjectId: String? = null
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
        id = id,
        name = name,
        destination = destination,
        startDate = startDateString,
        endDate = endDateString,
        lcObjectId = lcObjectId
    )
}

fun TripModel.toLcObject(): LCObject
{
    val tripLcObject = LCObject("Trip")
    tripLcObject.put("name", this.name)
    tripLcObject.put("destination", this.destination)
    tripLcObject.put("startDate", this.startDate.isoLocalDateToStr())
    tripLcObject.put("endDate", this.endDate.isoLocalDateToStr())
    return tripLcObject
}

