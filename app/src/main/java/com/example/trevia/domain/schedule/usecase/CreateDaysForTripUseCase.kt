package com.example.trevia.domain.schedule.usecase

import com.example.trevia.data.schedule.DayRepository
import com.example.trevia.data.schedule.Day
import com.example.trevia.domain.schedule.model.TripModel
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class CreateDaysForTripUseCase @Inject constructor(private val dayRepository: DayRepository)
{
    suspend operator fun invoke(tripModel: TripModel)
    {
        val dateFormatter= DateTimeFormatter.ISO_LOCAL_DATE
        val startDate=tripModel.startDate
        val endDate=tripModel.endDate
        val days=ChronoUnit.DAYS.between(
            startDate,
            endDate
        ).toInt()+1
        for (i in 0 until days) {
            dayRepository.insertDay(
                Day(
                    tripId = tripModel.id,
                    date = startDate.plusDays(i.toLong()).format(dateFormatter),
                    indexInTrip = i + 1
                )
            )
        }
    }
}