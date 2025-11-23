package com.example.trevia.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trevia.domain.schedule.model.TripModel
import com.example.trevia.domain.schedule.usecase.DeleteTripByIdUseCase
import com.example.trevia.domain.schedule.usecase.GetAllTripsUseCase
import com.example.trevia.utils.isoLocalDateToStr
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
import kotlin.collections.map
import kotlin.collections.sortedBy

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
            .map { trips ->
                TripListUiState(
                    trips = trips
                        .sortedBy { it.startDate }
                        .map { it.toUiState() }
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TripListUiState()
            )

    val tripListUiState: StateFlow<TripListUiState> = _tripListUiState


    // 扩展函数：TripModel -> TripItemUiState
    private fun TripModel.toUiState(): TripItemUiState {
        val daysUntilTrip = ChronoUnit.DAYS.between(LocalDate.now(), startDate).toInt()
        val tripDaysCount = ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1

        return TripItemUiState(
            tripId = id,
            tripName = name,
            tripLocation = destination,
            tripDateRange = "${startDate.isoLocalDateToStr()} ~ ${endDate.isoLocalDateToStr()}",
            daysUntilTrip = daysUntilTrip,
            tripDaysCount = tripDaysCount
        )
    }

    fun deleteTripById(tripId: Long)
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
    val tripId: Long = 0,
    val tripName: String = "",
    val tripLocation: String = "",
    val tripDateRange: String = "",
    val tripDaysCount: Int = 0,
    val daysUntilTrip: Int = 0
)
