package com.example.trevia.domain.location

import cn.leancloud.LCObject
import com.example.trevia.data.remote.leancloud.UploadDataRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UploadLocationImgMetaUseCase @Inject constructor(
    private val uploadDataRepository: UploadDataRepository
)
{
    suspend operator fun invoke(poiId: String,imgUrl:String)
    {
        val imgMeta = LCObject("LocationImageMeta").apply {
            put("poiId", poiId)
            put("imgUrl", imgUrl)
        }
        uploadDataRepository.uploadData(imgMeta)
    }
}