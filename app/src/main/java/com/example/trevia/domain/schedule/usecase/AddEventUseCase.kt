package com.example.trevia.domain.schedule.usecase

import com.example.trevia.data.local.schedule.EventRepository
import com.example.trevia.domain.schedule.model.EventModel
import jakarta.inject.Inject
import javax.inject.Singleton

@Singleton
class AddEventUseCase @Inject constructor(
    private val eventRepository: EventRepository
)
{
    suspend operator fun invoke(eventModel: EventModel)
    {
        eventRepository.insertEvent(eventModel)
    }
}