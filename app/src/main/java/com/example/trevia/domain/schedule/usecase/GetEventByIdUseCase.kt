package com.example.trevia.domain.schedule.usecase

import com.example.trevia.data.schedule.EventRepository
import com.example.trevia.domain.schedule.model.EventModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
class GetEventByIdUseCase @Inject constructor(private val eventRepository: EventRepository)
{
    suspend operator fun invoke(eventId: Long): EventModel?
    {
        return eventRepository.getEventById(eventId)
    }
}