package com.example.trevia.ui.trip

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel

class AddTripViewModel : ViewModel()
{
    private val _addTripUiState = mutableStateOf(AddTripUiState())
    val addTripUiState: State<AddTripUiState> = _addTripUiState

    fun updateAddTripUiState(update: AddTripUiState.() -> AddTripUiState)
    {
        _addTripUiState.value = _addTripUiState.value.update()
    }
}

data class AddTripUiState(
    val tripName: String = "",
    val tripLocation: String = "",
    val tripDateRange: String = "",
)

