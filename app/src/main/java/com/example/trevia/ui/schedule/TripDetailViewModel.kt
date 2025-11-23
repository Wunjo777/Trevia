package com.example.trevia.ui.schedule

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trevia.domain.schedule.model.DayWithEventsModel
import com.example.trevia.domain.schedule.model.TripWithDaysAndEventsModel
import com.example.trevia.domain.schedule.usecase.GetTripWithDaysAndEventsUseCase
import com.example.trevia.ui.navigation.TripDetailsDestination
import com.example.trevia.utils.isoLocalDateToStr
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getTripWithDaysAndEventsUseCase: GetTripWithDaysAndEventsUseCase
) : ViewModel()
{
    companion object
    {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val currentTripId: Long =
        checkNotNull(savedStateHandle[TripDetailsDestination.TRIP_ID_ARG])

    val tripDetailUiState: StateFlow<TripDetailUiState> =
        getTripWithDaysAndEventsUseCase(currentTripId)
            .map { trip ->
                trip?.toUiState() ?: TripDetailUiState.NotFound
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TripDetailUiState.Loading
            )

    // 扩展函数：TripWithDaysAndEventsModel -> TripDetailUiState.Success
    private fun TripWithDaysAndEventsModel.toUiState(): TripDetailUiState.Success =
        TripDetailUiState.Success(
            tripId = id,
            tripName = name,
            tripLocation = destination,
            tripDateRange = "${startDate.isoLocalDateToStr()} ~ ${endDate.isoLocalDateToStr()}",
            tripDaysCount = daysWithEvents.size,
            days = daysWithEvents.map { it.toUiState() }.sortedBy { it.indexInTrip }
        )

    // 扩展函数：DayWithEventsModel -> DayWithEventsUiState
    private fun DayWithEventsModel.toUiState(): DayWithEventsUiState =
        DayWithEventsUiState(
            dayId = id,
            date = date.isoLocalDateToStr(),
            events = events.map { EventUiState(it.id) }
        )
}

sealed interface TripDetailUiState
{
    object Loading : TripDetailUiState
    object NotFound : TripDetailUiState
    data class Success(
        val tripId: Long = 0,
        val tripName: String = "",
        val tripLocation: String = "",
        val tripDateRange: String = "",
        val tripDaysCount: Int = 0,
        val days: List<DayWithEventsUiState> = listOf()
    ) : TripDetailUiState
}

data class DayWithEventsUiState(
    val dayId: Long = 0,
    val date: String = "",
    val indexInTrip: Int = 0,
    val events: List<EventUiState> = listOf()
)

data class EventUiState(
    val eventId: Long = 0
)
