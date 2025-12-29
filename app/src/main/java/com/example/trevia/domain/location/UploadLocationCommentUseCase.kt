package com.example.trevia.domain.location

import cn.leancloud.LCObject
import com.example.trevia.data.remote.leancloud.UploadDataRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UploadLocationCommentUseCase @Inject constructor(private val uploadDataRepository: UploadDataRepository)
{
    suspend operator fun invoke(poiId: String, commentText: String)
    {
        val commentMeta = LCObject("LocationComment").apply {
            put("poiId", poiId)
            put("commentText", commentText)
        }
        uploadDataRepository.uploadData(commentMeta)
    }
}