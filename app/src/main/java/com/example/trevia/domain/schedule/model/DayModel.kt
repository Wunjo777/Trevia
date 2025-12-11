package com.example.trevia.domain.schedule.model

import com.example.trevia.data.schedule.Day
import com.example.trevia.utils.isoLocalDateToStr
import java.time.LocalDate

data class DayModel(
    val id: Long = 0,
    val tripId: Long,
    val date: LocalDate,
    val indexInTrip: Int,
    val lcObjectId: String? = null
)

fun DayModel.toDay() = Day(
    id = id,
    tripId = tripId,
    date = date.isoLocalDateToStr(),
    indexInTrip = indexInTrip,
    lcObjectId = lcObjectId
)
