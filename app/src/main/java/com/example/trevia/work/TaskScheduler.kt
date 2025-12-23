package com.example.trevia.work

import android.net.Uri
import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.trevia.work.workers.CreateAndUploadLargeImgWorker
import com.example.trevia.work.workers.DaySyncDownWorker
import com.example.trevia.work.workers.DaySyncUpWorker
import com.example.trevia.work.workers.EventSyncDownWorker
import com.example.trevia.work.workers.EventSyncUpWorker
import com.example.trevia.work.workers.PhotoSyncDownWorker
import com.example.trevia.work.workers.PhotoSyncUpWorker
import com.example.trevia.work.workers.TripSyncDownWorker
import com.example.trevia.work.workers.TripSyncUpWorker
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskScheduler @Inject constructor(
    private val workManager: WorkManager
)
{
    fun scheduleCreateAndUploadLargeImg(
        uri: Uri,
        photoId: Long,
        fileName: String, compressQuality: Int = 80,
        maxSize: Int = 1280,
        thumbnailSize: Int = 200
    )
    {
        val input = workDataOf(
            "uri" to uri.toString(),
            "photoId" to photoId,
            "fileName" to fileName,
            "compressQuality" to compressQuality,
            "maxSize" to maxSize,
            "thumbnailSize" to thumbnailSize
        )

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val request = OneTimeWorkRequestBuilder<CreateAndUploadLargeImgWorker>()
            .setInputData(input)
            .setConstraints(constraints)
            .build()

        workManager.enqueue(request)
    }

    fun scheduleSync()
    {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val tripSyncDownRequest = OneTimeWorkRequestBuilder<TripSyncDownWorker>()
            .setConstraints(constraints)
            .build()

        val tripSyncUpRequest = OneTimeWorkRequestBuilder<TripSyncUpWorker>()
            .setConstraints(constraints)
            .build()

        val daySyncDownRequest = OneTimeWorkRequestBuilder<DaySyncDownWorker>()
            .setConstraints(constraints)
            .build()

        val daySyncUpRequest = OneTimeWorkRequestBuilder<DaySyncUpWorker>()
            .setConstraints(constraints)
            .build()

        val eventSyncDownRequest = OneTimeWorkRequestBuilder<EventSyncDownWorker>()
            .setConstraints(constraints)
            .build()

        val eventSyncUpRequest = OneTimeWorkRequestBuilder<EventSyncUpWorker>()
            .setConstraints(constraints)
            .build()

        val photoSyncDownRequest = OneTimeWorkRequestBuilder<PhotoSyncDownWorker>()
            .setConstraints(constraints)
            .build()

        val photoSyncUpRequest = OneTimeWorkRequestBuilder<PhotoSyncUpWorker>()
            .setConstraints(constraints)
            .build()


        workManager.beginWith(tripSyncDownRequest)   // 下行
            .then(tripSyncUpRequest)      // 上行
            .then(daySyncDownRequest)   // 下行
            .then(daySyncUpRequest)     // 上行
            .then(eventSyncDownRequest) // 下行
            .then(eventSyncUpRequest)   // 上行
            .then(photoSyncDownRequest) // 下行
            .then(photoSyncUpRequest)   // 上行
            .enqueue()
        Log.d("test", "scheduleSync started")
    }
}