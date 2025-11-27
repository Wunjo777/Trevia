package com.example.trevia.domain.schedule.usecase

import com.example.trevia.data.schedule.EventRepository
import com.example.trevia.domain.schedule.model.EventModel
import com.example.trevia.domain.schedule.model.toEvent
import jakarta.inject.Inject

class UpdateEventUseCase @Inject constructor(private val eventRepository: EventRepository)
{
    suspend operator fun invoke(eventModel: EventModel) =
        eventRepository.updateEvent(eventModel)
}