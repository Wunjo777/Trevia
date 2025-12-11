package com.example.trevia.domain.imgupload.usecase

import com.example.trevia.data.schedule.PhotoRepository
import com.example.trevia.domain.imgupload.model.PhotoModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPhotosByTripIdUseCase @Inject constructor(private val photoRepository: PhotoRepository)
{
    operator fun invoke(tripId: Long): Flow<List<PhotoModel>> =
        photoRepository.getPhotosByTripIdStream(tripId)
}