package com.example.trevia.domain.imgupload.model

import cn.leancloud.LCObject
import com.example.trevia.data.local.schedule.Photo
import com.example.trevia.data.remote.SyncState

data class PhotoModel(
    val id: Long = 0,
    val tripId: Long,
    val eventId: Long?,
    val localOriginUri: String? = null,
    val largeImgUrl: String? = null,
    val thumbnailUrl: String? = null,
    val lcObjectId: String? = null,
    val syncState: SyncState = SyncState.PENDING,
    val updatedAt: Long = 0
)

fun PhotoModel.toPhoto() = Photo(
    id = this.id,
    tripId = this.tripId,
    eventId = this.eventId,
    localOriginUri = this.localOriginUri,
    largeImgUrl = this.largeImgUrl,
    thumbnailUrl = this.thumbnailUrl,
    lcObjectId = this.lcObjectId,
    syncState = this.syncState,
    updatedAt = this.updatedAt
)

fun PhotoModel.createNewLcObject(tripObjectId: String, eventObjectId: String? = null): LCObject
{
    val lcObject = LCObject("Photo")
    lcObject.put("trip", LCObject.createWithoutData("Trip", tripObjectId))
    lcObject.put("event", LCObject.createWithoutData("Event", eventObjectId))
    lcObject.put("largeImgUrl", this.largeImgUrl)
    lcObject.put("thumbnailUrl", this.thumbnailUrl)
    return lcObject
}
