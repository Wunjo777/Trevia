package com.example.trevia.domain.imgupload.usecase

import com.example.trevia.data.schedule.PhotoRepository
import com.example.trevia.domain.imgupload.model.PhotoModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class AddPhotoUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
)
{
    suspend operator fun invoke(photoModel: PhotoModel): Long
    {
        // 文件必须存在
        require(File(photoModel.thumbnailPath).exists()) { "Thumbnail file does not exist" }
        return photoRepository.addPhoto(photoModel)
    }
}