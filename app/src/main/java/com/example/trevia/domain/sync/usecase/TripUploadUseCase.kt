package com.example.trevia.domain.sync.usecase

import android.util.Log
import com.example.trevia.data.leancloud.SyncRepository
import com.example.trevia.data.schedule.TripRepository
import com.example.trevia.di.OfflineRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripUploadUseCase @Inject constructor(
    @OfflineRepo private val tripRepository: TripRepository,
    private val syncRepository: SyncRepository
)
{
    suspend operator fun invoke()
    {
        try
        {
            val trips = tripRepository.getTripsWithoutLcObjectId()
            if (trips.isEmpty())
            {
//                Log.d("test", "TripUploadUseCase: no need to upload.")
                return
            }

            val objectIds = syncRepository.uploadTrips(trips)
            tripRepository.updateTripsWithLcObjectId(trips, objectIds)
        } catch (e: Exception)
        {
            e.printStackTrace()
        }

    }
}