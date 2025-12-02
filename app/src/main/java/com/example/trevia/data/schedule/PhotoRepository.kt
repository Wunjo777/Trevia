package com.example.trevia.data.schedule

import com.example.trevia.domain.imgupload.model.PhotoModel
import com.example.trevia.domain.imgupload.model.toPhoto
import javax.inject.Inject

class PhotoRepository @Inject constructor(private val photoDao: PhotoDao)
{
    suspend fun addPhoto(photo: PhotoModel) = photoDao.insert(photo.toPhoto())
}