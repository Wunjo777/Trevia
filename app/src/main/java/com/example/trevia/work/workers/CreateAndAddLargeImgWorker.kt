package com.example.trevia.work.workers

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.trevia.domain.imgupload.usecase.CreateLargeImgUseCase
import com.example.trevia.domain.imgupload.usecase.UpdatePhotoUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CreateAndAddLargeImgWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val createLargeImgUseCase: CreateLargeImgUseCase,
    private val updatePhotoUseCase: UpdatePhotoUseCase
) : CoroutineWorker(context, params)
{
    @RequiresApi(Build.VERSION_CODES.Q)
    override suspend fun doWork(): Result
    {
        val uriString = inputData.getString("uri") ?: return Result.failure()
        val photoId = inputData.getLong("photoId", 0)
        val fileName = inputData.getString("fileName") ?: return Result.failure()
        val compressQuality = inputData.getInt("compressQuality", 80)
        val maxSize = inputData.getInt("maxSize", 1280)

        return try
        {
            val largeUri = createLargeImgUseCase(
                uriString.toUri(),
                fileName,
                compressQuality,
                maxSize
            )

            // update local DB
            updatePhotoUseCase.updateLargeImgPath(photoId, largeUri.path.toString())

            Result.success()

        } catch (e: Exception)
        {
            e.printStackTrace()
            Result.retry()
        }
    }
}