package com.example.trevia.domain.schedule.usecase

import com.example.trevia.data.local.schedule.EventRepository
import com.example.trevia.data.local.schedule.PhotoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteEventByIdUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val photoRepository: PhotoRepository
)
{
    suspend operator fun invoke(eventId: Long)
    {
        eventRepository.deleteEventById(eventId)
        photoRepository.deletePhotosByEventIds(listOf(eventId))
    }
}