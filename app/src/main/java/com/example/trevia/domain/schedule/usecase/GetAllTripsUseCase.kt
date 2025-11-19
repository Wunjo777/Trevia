package com.example.trevia.domain.schedule.usecase

import com.example.trevia.data.schedule.Trip
import com.example.trevia.data.schedule.TripRepository
import com.example.trevia.di.OfflineRepo
import com.example.trevia.domain.schedule.model.TripModel
import com.example.trevia.ui.schedule.TripItemUiState
import com.example.trevia.ui.schedule.TripListUiState
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class GetAllTripsUseCase @Inject constructor(
    @OfflineRepo private val tripRepository: TripRepository
) {
    operator fun invoke(): Flow<TripListUiState> {
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

        return tripRepository.getAllTripsStream()
            .map { trips ->

                val sortedTrips = trips.sortedBy { it.startDate }  // 按照 startDate 从最近到最远排序

                // 返回排序后的 TripListUiState
                TripListUiState(
                    trips = sortedTrips.map { tripModel ->
                        TripItemUiState(
                            tripId = tripModel.id,
                            tripName = tripModel.name,
                            tripLocation = tripModel.destination,
                            tripDateRange = "${tripModel.startDate.format(dateFormatter)} ~ ${tripModel.endDate.format(dateFormatter)}",
                            daysUntilTrip = ChronoUnit.DAYS.between(
                                LocalDate.now(),
                                tripModel.startDate
                            ).toInt(),
                            tripDaysCount = ChronoUnit.DAYS.between(
                                tripModel.startDate,
                                tripModel.endDate
                            ).toInt()
                        )
                    }
                )
            }
    }
}