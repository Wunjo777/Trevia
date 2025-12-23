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
import com.example.trevia.domain.imgupload.usecase.UploadImgToServerUseCase
import com.example.trevia.utils.LeanCloudFailureException
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CreateAndUploadLargeImgWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val createLargeImgUseCase: CreateLargeImgUseCase,
    private val uploadImgToServerUseCase: UploadImgToServerUseCase,
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
        val thumbnailSize = inputData.getInt("thumbnailSize", 200)

        return try
        {
            val imgBytes = createLargeImgUseCase(
                uriString.toUri(),
                compressQuality,
                maxSize
            )

            // upload to server
            val urlPair = uploadImgToServerUseCase(
                imgBytes,
                fileName,
                thumbnailSize
            )

            // update local DB
            updatePhotoUseCase.updateLargeImgUrlById(photoId, urlPair.first)
             updatePhotoUseCase.updateThumbnailUrlById(photoId, urlPair.second)

            Result.success()

        } catch (e: LeanCloudFailureException)
        {
            Log.w("WWW", "LeanCloudFailureException,retrying......", e)
            e.printStackTrace()
            Result.retry()
        } catch (e: Exception)
        {
            Log.w("EEE", "CreateAndUploadLargeImgWorker doWork failed", e)
            e.printStackTrace()
            Result.failure()
        }
    }
}