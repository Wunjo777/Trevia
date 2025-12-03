package com.example.trevia.data.schedule

import com.example.trevia.domain.imgupload.model.PhotoModel
import com.example.trevia.domain.imgupload.model.toPhoto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PhotoRepository @Inject constructor(private val photoDao: PhotoDao)
{
    suspend fun addPhoto(photo: PhotoModel): Long = photoDao.insert(photo.toPhoto())

    suspend fun updateLargeImgPath(id: Long, largeImgPath: String) =
        photoDao.updateLargeImgPath(id, largeImgPath)

    fun getAllPhotosStream(): Flow<List<PhotoModel>> =
        photoDao.getAllPhotos().map { photos -> photos.map { it.toPhotoModel() } }
}