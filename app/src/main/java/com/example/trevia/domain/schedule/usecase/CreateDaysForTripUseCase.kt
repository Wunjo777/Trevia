package com.example.trevia.domain.schedule.usecase

import com.example.trevia.data.local.schedule.DayRepository
import com.example.trevia.domain.schedule.model.DayModel
import com.example.trevia.domain.schedule.model.TripModel
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateDaysForTripUseCase @Inject constructor(private val dayRepository: DayRepository)
{
    suspend operator fun invoke(tripModel: TripModel)
    {
        val startDate=tripModel.startDate
        val endDate=tripModel.endDate
        val days=ChronoUnit.DAYS.between(
            startDate,
            endDate
        ).toInt()+1
        for (i in 0 until days) {
            dayRepository.insertDay(
                DayModel(
                    tripId = tripModel.id,
                    date = startDate.plusDays(i.toLong()),
                    indexInTrip = i + 1
                )
            )
        }
    }
}