package com.example.trevia.domain.imgupload.usecase

import com.example.trevia.data.local.schedule.PhotoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdatePhotoUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
)
{
    suspend fun updateLargeImgUrlById(id: Long, largeImgUrl: String) = photoRepository.updateLargeImgUrlById(id, largeImgUrl)

     suspend fun updateThumbnailUrlById(id: Long, thumbnailUrl: String) = photoRepository.updateThumbnailUrlById(id, thumbnailUrl)
}