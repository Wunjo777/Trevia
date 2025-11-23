package com.example.trevia.domain.schedule.model

import com.example.trevia.data.schedule.Day
import com.example.trevia.utils.isoLocalDateToStr
import java.time.LocalDate

data class DayModel(
    val id: Long=0,
    val tripId: Long,
    val date: LocalDate,
    val indexInTrip: Int
)

fun DayModel.toDay() = Day( tripId=tripId, date=date.isoLocalDateToStr(), indexInTrip=indexInTrip)
