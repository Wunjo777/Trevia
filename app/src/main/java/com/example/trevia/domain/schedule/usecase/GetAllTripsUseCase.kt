package com.example.trevia.domain.schedule.usecase

import com.example.trevia.data.schedule.Trip
import com.example.trevia.data.schedule.TripRepository
import com.example.trevia.di.OfflineRepo
import com.example.trevia.domain.schedule.model.TripModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetAllTripsUseCase @Inject constructor(
    @OfflineRepo private val tripRepository: TripRepository
)
{
    operator fun invoke(): Flow<List<TripModel>>
    {
        return tripRepository.getAllTripsStream()
    }
}