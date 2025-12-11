package com.example.trevia.work

import android.net.Uri
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.trevia.work.workers.CreateAndAddLargeImgWorker
import com.example.trevia.work.workers.TripUploadWorker
import androidx.work.Constraints
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

    fun scheduleUploadDataToLC()
    {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val request = OneTimeWorkRequestBuilder<TripUploadWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueue(request)
    }
}