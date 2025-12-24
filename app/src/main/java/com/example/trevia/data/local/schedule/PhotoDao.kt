package com.example.trevia.data.local.schedule

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.trevia.data.remote.SyncState
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao
{
    @Upsert
    suspend fun upsert(photo: Photo): Long

    @Upsert
    suspend fun upsertPhotos(photos: List<Photo>)

    @Query("UPDATE photos SET largeImgUrl = :largeImgUrl WHERE id = :id")
    suspend fun updateLargeImgUrlById(id: Long, largeImgUrl: String)

    @Query("UPDATE photos SET thumbnailUrl = :thumbnailUrl WHERE id = :id")
    suspend fun updateThumbnailUrlById(id: Long, thumbnailUrl: String)

    @Query("UPDATE photos SET syncState = :synced WHERE id IN (:ids)")
    suspend fun updatePhotosWithSynced(ids: List<Long>, synced: SyncState = SyncState.SYNCED)

    @Query("UPDATE photos SET syncState = :pending WHERE id IN (:ids)")
    suspend fun updatePhotosWithPending(ids: Set<Long>, pending: SyncState = SyncState.PENDING)

    @Query("UPDATE photos SET lcObjectId = :lcObjectId WHERE id = :photoId")
    suspend fun updatePhotoWithLcObjectId(photoId: Long, lcObjectId: String)

    @Query("UPDATE photos SET updatedAt = :updatedAt WHERE id = :photoId")
    suspend fun updatePhotoWithUpdatedAt(photoId: Long, updatedAt: Long)

    @Query("SELECT * FROM photos WHERE syncState IN (:syncStates)")
    suspend fun getPhotosBySyncState(syncStates: List<SyncState>): List<Photo>

    @Query("SELECT * FROM photos WHERE lcObjectId IN (:objectIds)")
    suspend fun getPhotosByObjectIds(objectIds: List<String>): List<Photo>


    @Query("SELECT * FROM photos WHERE syncState != :deleted")
    fun getAllPhotos(deleted: SyncState = SyncState.DELETED): Flow<List<Photo>>

    @Query("SELECT * FROM photos WHERE tripId = :tripId AND syncState != :deleted")
    fun getPhotosByTripId(tripId: Long, deleted: SyncState = SyncState.DELETED): Flow<List<Photo>>

    @Query("SELECT * FROM photos WHERE id = :photoId")
    suspend fun getPhotoById(photoId: Long): Photo?

    @Query("UPDATE photos SET syncState = :deleted WHERE eventId IN (:eventIds)")
    suspend fun softDeletePhotosByEventIds(
        eventIds: List<Long>,
        deleted: SyncState = SyncState.DELETED
    )

    @Query("UPDATE photos SET syncState = :deleted WHERE id IN (:ids)")
    suspend fun softDeletePhotosByIds(ids: Set<Long>, deleted: SyncState = SyncState.DELETED)

    @Query("DELETE FROM photos WHERE lcObjectId IN (:objectIds)")
    suspend fun hardDeletePhotosByObjectIds(objectIds: List<String>)


    @Query("UPDATE photos SET eventId = :eventId WHERE id IN (:photoIds)")
    suspend fun updatePhotosEventId(photoIds: Set<Long>, eventId: Long)

}