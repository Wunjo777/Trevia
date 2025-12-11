package com.example.trevia.domain.schedule.usecase

import com.example.trevia.data.schedule.EventRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteEventByIdUseCase @Inject constructor(private val eventRepository: EventRepository)
{
    suspend operator fun invoke(eventId: Long) = eventRepository.deleteEventById(eventId)
}