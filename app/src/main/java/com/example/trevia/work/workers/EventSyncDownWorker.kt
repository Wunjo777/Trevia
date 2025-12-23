package com.example.trevia.work.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.trevia.domain.sync.usecase.EventSyncDownUseCase
import com.example.trevia.domain.sync.usecase.TripSyncDownUseCase
import com.example.trevia.utils.LeanCloudFailureException
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class EventSyncDownWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val eventSyncDownUseCase: EventSyncDownUseCase,
) : CoroutineWorker(context, params)
{
    override suspend fun doWork(): Result
    {
        return try
        {
            eventSyncDownUseCase()
            Result.success()
        } catch (e: LeanCloudFailureException)
        {
            Log.w("WWW", "Fetch trips failed,retrying...", e)
            Result.retry()
        } catch (e: Exception)
        {
            Log.e("EEE", "EventSyncDownWorker doWork error", e)
            Result.failure()
        }
    }
}