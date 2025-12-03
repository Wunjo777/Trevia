package com.example.trevia.domain.imgupload.usecase

import com.example.trevia.data.schedule.PhotoRepository
import javax.inject.Inject

class UpdatePhotoUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
)
{
    suspend fun updateLargeImgPath(id: Long, largeImgPath: String) = photoRepository.updateLargeImgPath(id, largeImgPath)
}