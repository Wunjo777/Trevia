package com.example.trevia.domain.schedule.usecase

import com.example.trevia.data.local.schedule.TripRepository
import com.example.trevia.di.OfflineRepo
import com.example.trevia.domain.schedule.model.TripModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
class GetAllTripsUseCase @Inject constructor(
    @OfflineRepo private val tripRepository: TripRepository
) {
    operator fun invoke(): Flow<List<TripModel>> {

        return tripRepository.getAllTripsStream()

    }
}