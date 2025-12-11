package com.example.trevia.domain.schedule.usecase

import com.example.trevia.data.schedule.Trip
import com.example.trevia.data.schedule.TripRepository
import com.example.trevia.di.OfflineRepo
import com.example.trevia.domain.schedule.model.TripModel
import com.example.trevia.ui.schedule.TripItemUiState
import com.example.trevia.ui.schedule.TripListUiState
import com.example.trevia.utils.isoLocalDateToStr
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Singleton

@Singleton
class GetAllTripsUseCase @Inject constructor(
    @OfflineRepo private val tripRepository: TripRepository
) {
    operator fun invoke(): Flow<List<TripModel>> {

        return tripRepository.getAllTripsStream()

    }
}