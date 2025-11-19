package com.example.trevia.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trevia.domain.schedule.usecase.DeleteTripByIdUseCase
import com.example.trevia.domain.schedule.usecase.GetAllTripsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@HiltViewModel
class TripListViewModel @Inject constructor(
    getAllTripsUseCase: GetAllTripsUseCase,
    private val deleteTripByIdUseCase: DeleteTripByIdUseCase
) : ViewModel()
{
    companion object
    {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val _tripListUiState: StateFlow<TripListUiState> =
        getAllTripsUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TripListUiState()
            )

    val tripListUiState: StateFlow<TripListUiState> = _tripListUiState


    fun deleteTripById(tripId: Int)
    {
        viewModelScope.launch {
            deleteTripByIdUseCase(tripId)
        }
    }
}

data class TripListUiState(

    val trips: List<TripItemUiState> = listOf()
)

data class TripItemUiState(
    val tripId: Int = 0,
    val tripName: String = "",
    val tripLocation: String = "",
    val tripDateRange: String = "",
    val tripDaysCount: Int = 0,
    val daysUntilTrip: Int = 0
)
