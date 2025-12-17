package com.example.trevia.work

import android.net.Uri
import android.util.Log
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.trevia.work.workers.CreateAndAddLargeImgWorker
import com.example.trevia.work.workers.TripSyncUpWorker
import androidx.work.Constraints
import com.example.trevia.work.workers.TripSyncDownWorker
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskScheduler @Inject constructor(
    private val workManager: WorkManager
)
{
    fun scheduleCreateAndAddLargeImg(
        uri: Uri,
        photoId: Long,
        fileName: String, compressQuality: Int = 80,
        maxSize: Int = 1280
    )
    {
        val input = workDataOf(
            "uri" to uri.toString(),
            "photoId" to photoId,
            "fileName" to fileName,
            "compressQuality" to compressQuality,
            "maxSize" to maxSize
        )

        val request = OneTimeWorkRequestBuilder<CreateAndAddLargeImgWorker>()
            .setInputData(input)
            .build()

        workManager.enqueue(request)
    }

    fun scheduleSync()
    {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val downRequest = OneTimeWorkRequestBuilder<TripSyncDownWorker>()
            .setConstraints(constraints)
            .build()

        val upRequest = OneTimeWorkRequestBuilder<TripSyncUpWorker>()
            .setConstraints(constraints)
            .build()

        workManager.beginWith(downRequest)   // 下行
            .then(upRequest)      // 上行
            .enqueue()
        Log.d("test", "scheduleSync started")
    }
}