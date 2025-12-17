package com.example.trevia.work.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.trevia.domain.sync.usecase.TripSyncUpUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class TripSyncUpWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val tripSyncUpUseCase: TripSyncUpUseCase,
) : CoroutineWorker(context, params)
{
    override suspend fun doWork(): Result
    {
        return try
        {
            tripSyncUpUseCase()
            Result.success()
        } catch (e: Exception)
        {
            Log.e("EEE", "TripSyncUpWorker doWork error", e)
            Result.retry()
        }
    }
}