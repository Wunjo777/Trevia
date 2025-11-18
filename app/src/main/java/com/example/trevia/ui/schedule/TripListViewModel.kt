package com.example.trevia.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trevia.domain.schedule.usecase.GetAllTripsUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.format.DateTimeFormatter

class TripListViewModel @Inject constructor(
    private val getAllTripsUseCase: GetAllTripsUseCase
) : ViewModel()
{
    companion object
    {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val dateFormatter = DateTimeFormatter.ISO_DATE_TIME

    val tripListUiState: StateFlow<TripListUiState> =
        getAllTripsUseCase()
            .map { trips ->
                TripListUiState(
                    trips.map { tripModel ->
                        TripItemUiState(
                            tripId = tripModel.id,
                            tripName = tripModel.name,
                            tripLocation = tripModel.destination,
                            tripDateRange = "${tripModel.startDate.format(dateFormatter)} ~ ${
                                tripModel.endDate.format(dateFormatter)
                            }"
                        )
                    }
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TripListUiState()
            )
}

data class TripListUiState(
    val trips: List<TripItemUiState> = listOf()
)

data class TripItemUiState(
    val tripId: Int = 0,
    val tripName: String = "",
    val tripLocation: String = "",
    val tripDateRange: String = "",
)
