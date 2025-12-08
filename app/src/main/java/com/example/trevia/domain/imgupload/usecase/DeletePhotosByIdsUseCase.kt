package com.example.trevia.domain.imgupload.usecase

import com.example.trevia.data.schedule.PhotoRepository
import javax.inject.Inject

class DeletePhotosByIdsUseCase @Inject constructor(private val photoRepository: PhotoRepository)
{
    suspend operator fun invoke(ids: Set<Long>) = photoRepository.deletePhotosByIds(ids)
}