package com.example.trevia.ui.schedule

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.example.trevia.domain.schedule.model.TripModel
import com.example.trevia.utils.toDateString
import java.time.LocalDate

class AddTripViewModel : ViewModel()
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

    suspend fun saveTrip()
    {
        val trip = addTripUiState.value.toTripModel()
        // 调用usecase添加行程
        println("trip saved!")
    }
}

data class AddTripUiState(
    val tripName: String = "",
    val tripLocation: String = "",
    val tripDateRange: String = "",
)

fun AddTripUiState.toTripModel(): TripModel
{
    return TripModel(
        id = 0,
        name = tripName,
        destination = tripLocation,
        startDate = LocalDate.parse(tripDateRange.split(" ~ ")[0]),
        endDate = LocalDate.parse(tripDateRange.split(" ~ ")[1]),
        days = emptyList()
    )
}
