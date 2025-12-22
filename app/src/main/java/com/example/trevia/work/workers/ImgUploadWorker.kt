//package com.example.trevia.work.workers
//
//import android.content.Context
//import androidx.hilt.work.HiltWorker
//import androidx.work.CoroutineWorker
//import androidx.work.WorkerParameters
//import com.example.trevia.data.local.schedule.PhotoRepository
//import dagger.assisted.Assisted
//import dagger.assisted.AssistedInject
//
//@HiltWorker
//class ImgUploadWorker @AssistedInject constructor(
//    @Assisted context: Context,
//    @Assisted params: WorkerParameters,
//    private val photoRepository: PhotoRepository
//) : CoroutineWorker(context, params)
//{
//    override suspend fun doWork(): Result {
//        val photoId = inputData.getLong("photoId", -1)
//        if (photoId == -1L) return Result.failure()
//
//        val photo = photoRepository.getById(photoId)
//            ?: return Result.failure()
//
//        // 前置条件检查
//        if (photo.largePath == null) {
//            // 大图还没生成，稍后再来
//            return Result.retry()
//        }
//
//        return try {
//            photoRepository.updateUploadState(photoId, UPLOADING)
//
//            val remoteUrl = uploadService.upload(
//                filePath = photo.largePath
//            )
//
//            photoRepository.markUploaded(
//                photoId = photoId,
//                remoteUrl = remoteUrl
//            )
//
//            Result.success()
//        } catch (e: IOException) {
//            photoRepository.updateUploadState(photoId, FAILED)
//            Result.retry()
//        }
//    }
//}