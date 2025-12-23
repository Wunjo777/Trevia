package com.example.trevia.domain.imgupload.usecase

import com.example.trevia.data.local.schedule.PhotoRepository
import com.example.trevia.domain.imgupload.model.PhotoModel
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddPhotoUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
)
{
    suspend operator fun invoke(photoModel: PhotoModel): Long
    {
        return photoRepository.addPhoto(photoModel)
    }
}