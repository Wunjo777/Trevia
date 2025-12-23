package com.example.trevia.domain.imgupload.usecase

import cn.leancloud.LCFile
import com.example.trevia.data.local.schedule.PhotoRepository
import com.example.trevia.data.remote.leancloud.ImgUploadRepository
import com.example.trevia.domain.imgupload.model.PhotoModel
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UploadImgToServerUseCase @Inject constructor(
    private val imgUploadRepository: ImgUploadRepository
)
{
    suspend operator fun invoke(imgBytes: ByteArray, fileName: String,thumbnailSize:Int): Pair<String,String>
    {
        val urlPair = imgUploadRepository.uploadImgFile(fileName, imgBytes,thumbnailSize)
        return urlPair
    }
}