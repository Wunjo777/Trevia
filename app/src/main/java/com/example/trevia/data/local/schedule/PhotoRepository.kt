package com.example.trevia.data.local.schedule

import com.example.trevia.data.remote.SyncState
import com.example.trevia.domain.imgupload.model.PhotoModel
import com.example.trevia.domain.imgupload.model.toPhoto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor(private val photoDao: PhotoDao)
{
    suspend fun addPhoto(photo: PhotoModel): Long = photoDao.upsert(photo.toPhoto())

    suspend fun upsertPhotos(photos: List<PhotoModel>) = photoDao.upsertPhotos(photos.map { it.toPhoto() })

    suspend fun deletePhotosByIds(ids: Set<Long>) = photoDao.softDeletePhotosByIds(ids)

    suspend fun hardDeletePhotosByObjectIds(objectIds: List<String>) =
        photoDao.hardDeletePhotosByObjectIds(objectIds)

     suspend fun deletePhotosByEventIds(eventIds: List<Long>) =
        photoDao.softDeletePhotosByEventIds(eventIds)

    suspend fun updatePhotosWithSynced(photoIds: List<Long>) =
        photoDao.updatePhotosWithSynced(photoIds)

     suspend fun updatePhotosWithPending(photoIds: Set<Long>) =
        photoDao.updatePhotosWithPending(photoIds)

    suspend fun updatePhotoWithUpdatedAt(photoId: Long, updatedAt: Long) =
        photoDao.updatePhotoWithUpdatedAt(photoId, updatedAt)

    suspend fun updateLargeImgUrlById(id: Long, largeImgUrl: String) =
        photoDao.updateLargeImgUrlById(id, largeImgUrl)

     suspend fun updateThumbnailUrlById(id: Long, thumbnailUrl: String) =
        photoDao.updateThumbnailUrlById(id, thumbnailUrl)

    suspend fun updatePhotoWithLcObjectId(photoId: Long, lcObjectId: String) =
        photoDao.updatePhotoWithLcObjectId(photoId, lcObjectId)


    suspend fun updatePhotosEventId(photoIds: Set<Long>, eventId: Long) =
        photoDao.updatePhotosEventId(photoIds, eventId)

    fun getAllPhotosStream(): Flow<List<PhotoModel>> =
        photoDao.getAllPhotos().map { photos -> photos.map { it.toPhotoModel() } }

    fun getPhotosByTripIdStream(tripId: Long): Flow<List<PhotoModel>> =
        photoDao.getPhotosByTripId(tripId).map { photos -> photos.map { it.toPhotoModel() } }

    suspend fun getPhotosBySyncState(syncStates: List<SyncState>): List<PhotoModel> =
        photoDao.getPhotosBySyncState(syncStates).map { it.toPhotoModel() }

     suspend fun getPhotoMapByObjectIds(objectIds: List<String>): Map<String, PhotoModel> =
        photoDao.getPhotosByObjectIds(objectIds).map { it.toPhotoModel() }.associateBy { it.lcObjectId!! }


    suspend fun getPhotoById(photoId: Long): PhotoModel? =
        photoDao.getPhotoById(photoId)?.toPhotoModel()
}