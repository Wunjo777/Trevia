package com.example.trevia.domain.imgupload.usecase

import com.example.trevia.data.local.schedule.PhotoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovePhotosToEventUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
)
{
    suspend operator fun invoke(photoIds: Set<Long>, eventId: Long) =
        photoRepository.updatePhotosEventId(photoIds, eventId)
}