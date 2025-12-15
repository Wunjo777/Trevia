package com.example.trevia.domain.imgupload.usecase

import com.example.trevia.data.local.schedule.PhotoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdatePhotoUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
)
{
    suspend fun updateLargeImgPath(id: Long, largeImgPath: String) = photoRepository.updateLargeImgPath(id, largeImgPath)
}