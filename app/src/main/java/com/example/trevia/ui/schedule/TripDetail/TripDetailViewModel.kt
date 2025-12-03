package com.example.trevia.ui.schedule.TripDetail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trevia.domain.amap.usecase.GetInputTipsUseCase
import com.example.trevia.domain.schedule.model.DayWithEventsModel
import com.example.trevia.domain.schedule.model.TripWithDaysAndEventsModel
import com.example.trevia.domain.schedule.usecase.GetTripWithDaysAndEventsUseCase
import com.example.trevia.ui.navigation.TripDetailsDestination
import com.example.trevia.utils.isoLocalDateToStr
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.example.trevia.domain.amap.model.toLocationTipUiState
import com.example.trevia.domain.schedule.model.EventModel
import com.example.trevia.domain.schedule.usecase.AddEventUseCase
import com.example.trevia.domain.schedule.usecase.DeleteEventByIdUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime

@HiltViewModel
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class TripDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getTripWithDaysAndEventsUseCase: GetTripWithDaysAndEventsUseCase,
    getInputTipsUseCase: GetInputTipsUseCase,
    private val addEventUseCase: AddEventUseCase,
    private val deleteEventByIdUseCase: DeleteEventByIdUseCase
) : ViewModel()
{
    companion object
    {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val currentTripId: Long =
        checkNotNull(savedStateHandle[TripDetailsDestination.TRIP_ID_ARG])

    private val _selectedDayId = MutableStateFlow<Long?>(null)
    val selectedDayId: StateFlow<Long?> = _selectedDayId

    fun onSelectedDayChange(dayId: Long?) {
        _selectedDayId.value = dayId
    }

    //region 创建TripDetailUiState
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
            days = daysWithEvents.sortedBy { it.indexInTrip }.map { it.toUiState() }
        )

    // 扩展函数：DayWithEventsModel -> DayWithEventsUiState
    private fun DayWithEventsModel.toUiState(): DayWithEventsUiState =
        DayWithEventsUiState(
            dayId = id,
            date = date.isoLocalDateToStr(),
            indexInTrip = indexInTrip,
            events = events.sortedBy { it.startTime }.map {
                EventUiState(
                    it.id,
//                    it.dayId,
                    it.location,
                    it.address,
                    formatTimeRange(it.startTime, it.endTime),
                    it.description ?: ""
                )
            }
        )

    fun formatTimeRange(start: LocalTime?, end: LocalTime?): String
    {
        val startStr = start?.toString() ?: ""
        val endStr = end?.toString() ?: ""

        return if (startStr.isEmpty() && endStr.isEmpty())
        {
            ""
        }
        else
        {
            "$startStr ~ $endStr"
        }
    }
    //endregion

    fun addEventByLocation(
        dayId: Long,
        locationName: String,
        address: String,
    )
    {
        val eventModel = EventModel(
            dayId = dayId,
            location = locationName,
            address = address,
            startTime = null,
            endTime = null,
            description = null
        )
        viewModelScope.launch {
            addEventUseCase(eventModel)
        }

    }

     fun deleteEventById(eventId: Long)
    {
         viewModelScope.launch {
             deleteEventByIdUseCase(eventId)
         }
    }

    //region 弹出TipList
    // 用户输入关键词
    private val _keyword = MutableStateFlow("")
    val keyword: StateFlow<String> = _keyword

    // 提示列表，StateFlow 供 Compose 直接收集

    val tips: StateFlow<List<LocationTipUiState>> = _keyword
        .debounce(300)      // 防抖 300ms
        .filter { it.isNotBlank() }      // 忽略空输入
        .distinctUntilChanged()          // 相同关键词不重复请求
        .flatMapLatest { keyword ->
            flow {
                val location = if (tripDetailUiState.value is TripDetailUiState.Success)
                    (tripDetailUiState.value as TripDetailUiState.Success).tripLocation
                else ""
                val result = getInputTipsUseCase(keyword, location)
                emit(result)
            }.catch { e ->
                emit(emptyList())
            } // 出错显示空列表
        }.map { tipList -> tipList.map { tip -> tip.toLocationTipUiState() } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // 当用户输入变化时调用
    fun onKeywordChanged(newKeyword: String)
    {
        _keyword.value = newKeyword
    }
    //endregion
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
    val eventId: Long = 0,
//    val dayId: Long = 0,
    val location: String = "",
    val address: String = "",
    val timeRange: String = "",
    val description: String = ""
)

data class LocationTipUiState(
    val tipId: String = "",
    val name: String = "",
    val address: String = ""
)
