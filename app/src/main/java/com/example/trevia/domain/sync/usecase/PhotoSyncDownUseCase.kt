package com.example.trevia.domain.sync.usecase

import com.example.trevia.data.local.SyncDatastoreRepository
import com.example.trevia.data.local.schedule.PhotoRepository
import com.example.trevia.data.remote.SyncState
import com.example.trevia.data.remote.leancloud.SyncPhotoRepository
import com.example.trevia.domain.imgupload.model.PhotoModel
import com.example.trevia.domain.schedule.model.TripModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoSyncDownUseCase @Inject constructor(
    private val syncDatastoreRepository: SyncDatastoreRepository,
    private val syncPhotoRepository: SyncPhotoRepository,
    private val photoRepository: PhotoRepository
)
{
    suspend operator fun invoke()
    {
        val lastSyncTime = syncDatastoreRepository.getDayLastSyncTime()

        val changedPhotos = syncPhotoRepository.getPhotosAfter(lastSyncTime)

        if (changedPhotos.isEmpty())
        {
            return
        }

        val upserts = changedPhotos.filter { it.syncState == SyncState.SYNCED }
        val deletes = changedPhotos.filter { it.syncState == SyncState.DELETED }

        photoRepository.hardDeletePhotosByObjectIds(deletes.map { it.lcObjectId!! })

        val upsertsPhotoMap =
            photoRepository.getPhotoMapByObjectIds(upserts.map { it.lcObjectId!! })

        val photosToArchive = mutableListOf<PhotoModel>()

        val photosToUpsert = upserts.mapNotNull { photo ->
            val currentPhoto = upsertsPhotoMap[photo.lcObjectId!!]
            when
            {
                currentPhoto == null                                                                     -> photo//服务端新的，直接插入
                currentPhoto.updatedAt >= photo.updatedAt || currentPhoto.syncState == SyncState.DELETED -> null//刚上传和本地删除的，不更新
                else                                                                                     ->
                {//需要更新
                    if (currentPhoto.syncState == SyncState.PENDING)
                    {//本地存在冲突，先归档
                        photosToArchive.add(currentPhoto)
                    }
                    photo.copy(id = currentPhoto.id)
                }
            }
        }

        if(photosToArchive.isNotEmpty())
        {
           TODO("归档冲突的照片")
        }

        if (photosToUpsert.isNotEmpty())
        {
            photoRepository.upsertPhotos(photosToUpsert)
        }

        syncDatastoreRepository.setPhotoLastSyncTime(changedPhotos.last().updatedAt)
    }
}