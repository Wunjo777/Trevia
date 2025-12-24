package com.example.trevia.work.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.trevia.domain.sync.usecase.DaySyncDownUseCase
import com.example.trevia.utils.LeanCloudFailureException
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DaySyncDownWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val daySyncDownUseCase: DaySyncDownUseCase,
) : CoroutineWorker(context, params)
{
    override suspend fun doWork(): Result
    {
        return try
        {
            daySyncDownUseCase()
            Result.success()
        } catch (e: LeanCloudFailureException)
        {
            val trace = Throwable().stackTrace
            val caller = trace.getOrNull(1) // 0 是 Throwable 自身，1 是调用 Log 的地方
            Log.w("WWW", "Fetch days failed at ${caller?.fileName}:${caller?.lineNumber},retrying...", e)
            Result.retry()
        } catch (e: Exception)
        {
            val trace = Throwable().stackTrace
            val caller = trace.getOrNull(1) // 0 是 Throwable 自身，1 是调用 Log 的地方
            Log.e("EEE", "DaySyncDownWorker doWork error at ${caller?.fileName}:${caller?.lineNumber}", e)
            Result.failure()
        }
    }
}