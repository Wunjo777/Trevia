package com.example.trevia.domain.schedule.usecase

import android.util.Log
import com.example.trevia.data.local.schedule.DayRepository
import com.example.trevia.data.local.schedule.EventRepository
import com.example.trevia.data.local.schedule.TripRepository
import com.example.trevia.di.OfflineRepo
import jakarta.inject.Inject
import java.lang.Exception
import javax.inject.Singleton

@Singleton
class DeleteTripByIdWithDaysAndEventsUseCase @Inject constructor(
    @OfflineRepo private val tripRepository: TripRepository,
    private val dayRepository: DayRepository,
    private val eventRepository: EventRepository
)
{
    suspend operator fun invoke(tripId: Long)
    {
        try
        {
            tripRepository.deleteTripById(tripId)
            val dayIds = dayRepository.getDayIdsByTripId(tripId)
            dayRepository.deleteDaysByIds(dayIds)
            eventRepository.deleteEventsByDayIds(dayIds)
        } catch (e: Exception)
        {
            Log.e("EEE", "DeleteTripByIdWithDaysAndEventsUseCase: $e")
        }
    }
}
