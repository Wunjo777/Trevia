package com.example.trevia.data.remote.leancloud

import cn.leancloud.LCObject
import com.example.trevia.domain.location.model.CommentModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLocationDataRepository @Inject constructor(private val service: LeanCloudService)
{
    suspend fun getLocationImgUrls(poiId: String): List<String>
    {
        val lcObjects: List<LCObject> = service.getLocationDataByPoiId(poiId, "LocationImageMeta")
        return lcObjects.mapNotNull { it.getString("imageUrl") }
    }

    suspend fun getLocationComment(poiId: String): List<CommentModel>
    {
        val lcObjects: List<LCObject> = service.getLocationDataByPoiId(poiId, "LocationComment")
        return lcObjects.map { CommentModel(content = it.getString("commentText")) }
    }
}