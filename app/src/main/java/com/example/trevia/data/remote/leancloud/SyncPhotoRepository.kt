package com.example.trevia.data.remote.leancloud

import cn.leancloud.LCObject
import com.example.trevia.data.local.schedule.EventRepository
import com.example.trevia.data.local.schedule.TripRepository
import com.example.trevia.data.remote.SyncState
import com.example.trevia.data.remote.toLcObjectUpdateIsDelete
import com.example.trevia.di.OfflineRepo
import com.example.trevia.domain.imgupload.model.PhotoModel
import com.example.trevia.domain.imgupload.model.createNewLcObject
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncPhotoRepository @Inject constructor(
    private val service: LeanCloudService,
    @OfflineRepo private val tripRepository: TripRepository,
    private val eventRepository: EventRepository
)
{
    suspend fun upsertPhotos(eventObjectIdToPhotoModel: List<Triple<String, String?, PhotoModel>>): UploadResult
    {
        return service.upsertDatas(eventObjectIdToPhotoModel.map { (tripObjectId, eventObjectId, photoModel) ->
            Pair(
                photoModel.id,
                photoModel.createNewLcObject(tripObjectId, eventObjectId)
            )
        })
    }

    suspend fun softDeletePhotos(photoModels: List<PhotoModel>): Map<Long, String>
    {
        val responses = service.softDeleteDatas(photoModels.map {
            toLcObjectUpdateIsDelete(
                "Photo",
                it.lcObjectId
            )
        })

        val idMap = mutableMapOf<Long, String>()

        // responses 与 photoModels 一一对应
        //第一次上传的photo，获取返回的lcObjectId
        photoModels.indices
            .filter { photoModels[it].lcObjectId == null } // 筛选出 lcObjectId 为 null 的元素
            .forEach { index ->
                val success = responses.getJSONObject(index)
                val lcObjectId = success.getString("objectId")
                idMap[photoModels[index].id] = lcObjectId
            }

        return idMap
    }

    suspend fun getPhotosAfter(lastSyncTime: Long): List<PhotoModel>
    {
        return service.getDatasAfter(Date(lastSyncTime), "Photo").map { it.toPhotoModel() }
    }

    private suspend fun LCObject.toPhotoModel(): PhotoModel
    {
        val isDeleted = this.getBoolean("isDeleted")
        return PhotoModel(
            tripId = if (!isDeleted)
            {
                val tripObjectId = this.getLCObject<LCObject>("trip").objectId
                tripRepository.getTripIdByLcObjectId(tripObjectId)!!
            }
            else -1,//被删除的photo下行同步时不再映射本地外键
            eventId = if (!isDeleted)
            {
                val eventObjectId = this.getLCObject<LCObject>("event").objectId
                eventRepository.getEventIdByLcObjectId(eventObjectId)
            }
            else -1,//被删除的photo下行同步时不再映射本地外键
            largeImgUrl = this.getString("largeImgUrl"),
            thumbnailUrl = this.getString("thumbnailUrl"),
            lcObjectId = this.objectId,
            syncState = if (isDeleted) SyncState.DELETED else SyncState.SYNCED,
            updatedAt = this.getDate("updatedAt").time,
        )
    }
}