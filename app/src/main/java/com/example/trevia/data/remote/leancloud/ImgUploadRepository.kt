package com.example.trevia.data.remote.leancloud

import cn.leancloud.LCFile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImgUploadRepository @Inject constructor(
    private val service: LeanCloudService)
{
    suspend fun uploadImgFile(imgName: String, imgBytes: ByteArray,thumbnailSize:Int):Pair<String,String>
    {
        val imgFile = LCFile(imgName, imgBytes)
        val lcFile=service.uploadFile(imgFile)
        return Pair(lcFile.url,lcFile.getThumbnailUrl(true,thumbnailSize,thumbnailSize))
    }
}