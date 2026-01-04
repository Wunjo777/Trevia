package com.example.trevia.domain.schedule.model

import cn.leancloud.LCObject
import com.example.trevia.data.local.schedule.Day
import com.example.trevia.data.remote.SyncState
import com.example.trevia.utils.isoLocalDateToStr
import java.time.LocalDate

data class DayModel(
    val id: Long = 0,
    val tripId: Long,
    val date: LocalDate,
    val indexInTrip: Int,
    val lcObjectId: String? = null,
    val syncState: SyncState = SyncState.PENDING,
    val updatedAt: Long = 0
)

fun DayModel.toDay() = Day(
    id = id,
    tripId = tripId,
    date = date.isoLocalDateToStr(),
    indexInTrip = indexInTrip,
    lcObjectId = lcObjectId,
    syncState = syncState,
    updatedAt = updatedAt
)

fun DayModel.createNewLcObject(tripObjectId: String):LCObject
{
    val dayLcObject = LCObject("Day")
    dayLcObject.put("trip",LCObject.createWithoutData("Trip",tripObjectId))
    dayLcObject.put("date",this.date.isoLocalDateToStr())
    dayLcObject.put("indexInTrip",this.indexInTrip)
    dayLcObject.put("isDeleted", false)
    return dayLcObject
}
