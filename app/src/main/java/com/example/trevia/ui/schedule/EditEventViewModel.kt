package com.example.trevia.ui.schedule

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trevia.domain.schedule.model.EventModel
import com.example.trevia.domain.schedule.usecase.GetEventByIdUseCase
import com.example.trevia.domain.schedule.usecase.UpdateEventUseCase
import com.example.trevia.ui.navigation.EditEventDestination
import com.example.trevia.utils.toLocalTime
import com.example.trevia.utils.toTimeString
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class EditEventViewModel @Inject constructor(
    private val getEventByIdUseCase: GetEventByIdUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel()
{

    private val currentEventId: Long =
        checkNotNull(savedStateHandle[EditEventDestination.EVENT_ID_ARG])

    // UI 层可观察的可变 state
    private val _editEventUiState = mutableStateOf<EditEventUiState>(EditEventUiState.Loading)
    val editEventUiState: State<EditEventUiState> = _editEventUiState

    init
    {
        viewModelScope.launch {
            val eventModel = getEventByIdUseCase(currentEventId) // nullable
            if (eventModel == null)
            {
                _editEventUiState.value = EditEventUiState.NotFound
            }
            else
            {
                _editEventUiState.value = EditEventUiState.Success(eventModel.toEventInfoUiState())
            }
        }
    }

    fun updateEventInfo(update: EventInfoUiState.() -> EventInfoUiState)
    {
        val current = _editEventUiState.value
        if (current is EditEventUiState.Success)
        {
            _editEventUiState.value = EditEventUiState.Success(current.eventInfoUiState.update())
        }
    }

    fun updateEvent(onResult: (Boolean) -> Unit) {
        val current = _editEventUiState.value
        if (current is EditEventUiState.Success) {
            viewModelScope.launch {
                try {
                    updateEventUseCase(current.eventInfoUiState.toEventModel())
                    onResult(true)
                } catch (e: Exception) {
                    onResult(false)
                }
            }
        }
    }

}

private fun EventModel.toEventInfoUiState(): EventInfoUiState
{
    return EventInfoUiState(
        id = id,
        dayId = dayId,
        location = location,
        address = address,
        latitude = latitude,
        longitude = longitude,
        startTime = startTime?.toTimeString() ?: "",
        endTime = endTime?.toTimeString() ?: "",
        description = description ?: ""
    )
}

private fun EventInfoUiState.toEventModel(): EventModel
{
    return EventModel(
        id = id,
        dayId = dayId,
        location = location,
        address = address,
        latitude = latitude,
        longitude = longitude,
        startTime = startTime.toLocalTime(),
        endTime = endTime.toLocalTime(),
        description = description
    )
}

data class EventInfoUiState(
    val id: Long = 0,
    val dayId: Long = 0,
    val location: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val startTime: String = "",
    val endTime: String = "",
    val description: String = ""
)

sealed class EditEventUiState
{
    object Loading : EditEventUiState()
    data class Success(val eventInfoUiState: EventInfoUiState) : EditEventUiState()
    object NotFound : EditEventUiState()
}

sealed class UpdateEventUiState
{
    object Success : UpdateEventUiState()
    object Failed : UpdateEventUiState()
}
