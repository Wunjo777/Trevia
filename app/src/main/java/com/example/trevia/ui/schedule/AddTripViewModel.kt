package com.example.trevia.ui.schedule

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trevia.domain.schedule.model.TripModel
import com.example.trevia.domain.schedule.usecase.AddTripResult
import com.example.trevia.domain.schedule.usecase.AddTripUseCase
import com.example.trevia.utils.strToIsoLocalDate
import com.example.trevia.utils.toDateString
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.time.LocalDate

@HiltViewModel
class AddTripViewModel @Inject constructor(private val addTripUseCase: AddTripUseCase) : ViewModel()
{
    private val _addTripUiState = mutableStateOf(AddTripUiState())
    val addTripUiState: State<AddTripUiState> = _addTripUiState

    fun updateAddTripUiState(update: AddTripUiState.() -> AddTripUiState)
    {
        _addTripUiState.value = _addTripUiState.value.update()
    }

    // 用于更新日期范围
    fun selectedDateRangeToString(datePair: Pair<Long?, Long?>): String
    {
        val startStr = datePair.first?.toDateString() ?: ""
        val endStr = datePair.second?.toDateString() ?: ""
        return if (startStr.isNotEmpty() && endStr.isNotEmpty())
        {
            "$startStr ~ $endStr"
        }
        else
        {
            ""
        }
    }

    fun saveTrip()
    {
        val tripModel = addTripUiState.value.toTripModel()
        viewModelScope.launch {
            val saveTripResult = addTripUseCase(tripModel)
            updateAddTripUiState { copy(saveTripResult = saveTripResult) }
        }
    }

    fun clearSaveTripResult()
    {
        updateAddTripUiState { copy(saveTripResult = null) }
    }
}


data class AddTripUiState(
    val tripName: String = "",
    val tripLocation: String = "",
    val tripDateRange: String = "",
    val saveTripResult: AddTripResult? = null
)

fun AddTripUiState.toTripModel(): TripModel
{
    return TripModel(
        name = tripName,
        destination = tripLocation,
        startDate = if (tripDateRange.isNotEmpty()) tripDateRange.split(" ~ ")[0].strToIsoLocalDate() else LocalDate.MIN,
        endDate = if (tripDateRange.isNotEmpty()) tripDateRange.split(" ~ ")[1].strToIsoLocalDate() else LocalDate.MIN,
    )
}
