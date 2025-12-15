package com.example.trevia.domain.schedule.usecase

import com.example.trevia.data.local.schedule.TripRepository
import com.example.trevia.di.OfflineRepo
import jakarta.inject.Inject
import java.lang.Exception
import javax.inject.Singleton

@Singleton
class DeleteTripByIdUseCase @Inject constructor(
    @OfflineRepo private val tripRepository: TripRepository
)
{
    suspend operator fun invoke(tripId: Long)
    {
        try
        {
            tripRepository.deleteTripById(tripId)
        } catch (e: Exception)
        {
        }
    }
}
