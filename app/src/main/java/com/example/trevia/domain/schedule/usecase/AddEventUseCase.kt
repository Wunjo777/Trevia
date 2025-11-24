package com.example.trevia.domain.schedule.usecase

import com.example.trevia.data.schedule.EventRepository
import com.example.trevia.di.OfflineRepo
import com.example.trevia.domain.schedule.model.EventModel
import jakarta.inject.Inject

class AddEventUseCase @Inject constructor(
    @OfflineRepo private val eventRepository: EventRepository
)
{
    suspend operator fun invoke(eventModel: EventModel)
    {

    }
}