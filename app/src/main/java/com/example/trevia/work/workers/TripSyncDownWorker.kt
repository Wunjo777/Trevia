package com.example.trevia.work.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.trevia.domain.sync.usecase.TripSyncDownUseCase
import com.example.trevia.domain.sync.usecase.TripSyncUpUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class TripSyncDownWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val tripSyncDownUseCase: TripSyncDownUseCase,
) : CoroutineWorker(context, params)
{
    override suspend fun doWork(): Result
    {
        return try
        {
            tripSyncDownUseCase()
            Result.success()
        } catch (e: Exception)
        {
            throw(e)
//            Result.retry()
        }
    }
}