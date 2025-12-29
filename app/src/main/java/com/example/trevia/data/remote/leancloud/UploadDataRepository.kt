package com.example.trevia.data.remote.leancloud

import cn.leancloud.LCObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UploadDataRepository @Inject constructor(private val service: LeanCloudService)
{
    suspend fun uploadData(data: LCObject)
    {
        service.upsertData(data)
    }
}