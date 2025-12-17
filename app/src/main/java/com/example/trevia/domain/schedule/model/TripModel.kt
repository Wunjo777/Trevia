package com.example.trevia.domain.schedule.model

import cn.leancloud.LCObject
import com.example.trevia.data.local.schedule.Trip
import com.example.trevia.data.remote.SyncState
import com.example.trevia.utils.isoLocalDateToStr
import java.time.LocalDate

data class TripModel(
    val id: Long = 0,
    val name: String,
    val destination: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val lcObjectId: String? = null,
    val syncState: SyncState = SyncState.PENDING,
    val updatedAt: Long=0
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
        lcObjectId = lcObjectId,
        syncState = syncState,
        updatedAt = updatedAt
    )
}

fun TripModel.toLcObject(): LCObject
{
    val tripLcObject =
        if (lcObjectId != null) LCObject.createWithoutData("Trip", lcObjectId) else LCObject("Trip")
    tripLcObject.put("name", this.name)
    tripLcObject.put("destination", this.destination)
    tripLcObject.put("startDate", this.startDate.isoLocalDateToStr())
    tripLcObject.put("endDate", this.endDate.isoLocalDateToStr())
    tripLcObject.put("isDeleted", false)
    return tripLcObject
}

fun TripModel.toLcObjectUpdateIsDelete(): LCObject
{
    val tripLcObject =LCObject.createWithoutData("Trip", this.lcObjectId)
    tripLcObject.put("isDeleted", true)
    return tripLcObject
}
