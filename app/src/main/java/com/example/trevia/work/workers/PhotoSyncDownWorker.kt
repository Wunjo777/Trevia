package com.example.trevia.work.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.trevia.domain.sync.usecase.PhotoSyncDownUseCase
import com.example.trevia.utils.LeanCloudFailureException
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PhotoSyncDownWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val photoSyncDownUseCase: PhotoSyncDownUseCase,
) : CoroutineWorker(context, params)
{
    override suspend fun doWork(): Result
    {
        return try
        {
            photoSyncDownUseCase()
            Result.success()
        } catch (e: LeanCloudFailureException)
        {
            Log.w("WWW", "Fetch photos failed,retrying...", e)
            Result.retry()
        } catch (e: Exception)
        {
            Log.e("EEE", "PhotoSyncDownWorker doWork error", e)
            Result.failure()
        }
    }
}