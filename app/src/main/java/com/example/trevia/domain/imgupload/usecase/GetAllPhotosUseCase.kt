package com.example.trevia.domain.imgupload.usecase

import com.example.trevia.data.local.schedule.PhotoRepository
import com.example.trevia.domain.imgupload.model.PhotoModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

import javax.inject.Singleton

@Singleton
class GetAllPhotosUseCase @Inject constructor(private val photoRepository: PhotoRepository)
{
    operator fun invoke(): Flow<List<PhotoModel>> = photoRepository.getAllPhotosStream()
}