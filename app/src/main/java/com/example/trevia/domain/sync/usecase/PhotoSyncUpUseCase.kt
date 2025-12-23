package com.example.trevia.domain.sync.usecase

import com.example.trevia.data.local.schedule.EventRepository
import com.example.trevia.data.local.schedule.PhotoRepository
import com.example.trevia.data.local.schedule.TripRepository
import com.example.trevia.data.remote.SyncState
import com.example.trevia.data.remote.leancloud.SyncPhotoRepository
import com.example.trevia.di.OfflineRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoSyncUpUseCase @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val eventRepository: EventRepository,
    @OfflineRepo private val tripRepository: TripRepository,
    private val syncPhotoRepository: SyncPhotoRepository
)
{
    suspend operator fun invoke()
    {
        val photos =
            photoRepository.getPhotosBySyncState(listOf(SyncState.PENDING, SyncState.DELETED))
        if (photos.isEmpty())
        {
            return
        }

        //确保只有上传完成的照片才会被同步
        val photosUploaded = photos.filter { it.thumbnailUrl != null && it.largeImgUrl != null }

        val eventIds = photosUploaded.map { it.eventId!! }.distinct()
        val events = eventRepository.getEventsByIds(eventIds)
        val eventMap = events.associateBy { it.id }

        val tripIds = photosUploaded.map { it.tripId }.distinct()
        val trips = tripRepository.getTripsByIds(tripIds)
        val tripMap = trips.associateBy { it.id }

        var syncablePhotos = photosUploaded.filter { photo ->
            val trip = tripMap[photo.tripId]
            trip != null && trip.syncState == SyncState.SYNCED
        }

        syncablePhotos = syncablePhotos.filter { photo ->
            if (photo.eventId == null) return@filter true
            val event = eventMap[photo.eventId]!!
            event.syncState != SyncState.DELETED && event.lcObjectId != null
        }

        val upserts = syncablePhotos.filter { it.syncState == SyncState.PENDING }//新建的
        val deletes = syncablePhotos.filter { it.syncState == SyncState.DELETED }//删除的
        val lcObjectIdUpdates = mutableMapOf<Long, String>()

        if (deletes.isNotEmpty())
        {
            val idMap = syncPhotoRepository.softDeletePhotos(deletes)
            lcObjectIdUpdates.putAll(idMap)
            //删除不需要指定外键信息
//                TODO("hard delete days on LC after 7 days")
        }

        if (upserts.isNotEmpty())
        {
            val uploadResult =
                syncPhotoRepository.upsertPhotos(upserts.map { photo ->
                    Triple(
                        tripMap[photo.tripId]!!.lcObjectId!!,
                        photo.eventId?.let { eventMap[it]!!.lcObjectId!! },
                        photo
                    )
                })
            val idMap = uploadResult.dataIdToLcObjectId
            val updatedAtList = uploadResult.updatedAtList

            lcObjectIdUpdates.putAll(idMap)

            updatedAtList.forEachIndexed { index, updatedAt ->
                photoRepository.updatePhotoWithUpdatedAt(upserts[index].id, updatedAt)
            }

        }

        if (lcObjectIdUpdates.isNotEmpty())
        {
            lcObjectIdUpdates.forEach { (photoId, lcObjectId) ->
                photoRepository.updatePhotoWithLcObjectId(photoId, lcObjectId)
            }
        }
        //所有数据上传完成后，将本地数据标记为已上传
        photoRepository.updatePhotosWithSynced(upserts.map { it.id })
    }
}