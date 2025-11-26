package com.example.trevia.ui.schedule

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

@HiltViewModel
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class TripDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getTripWithDaysAndEventsUseCase: GetTripWithDaysAndEventsUseCase,
    getInputTipsUseCase: GetInputTipsUseCase
) : ViewModel()
{
    init
    {
        Log.d("SearchVM", "TripDetailViewModel created")
    }

    companion object
    {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val currentTripId: Long =
        checkNotNull(savedStateHandle[TripDetailsDestination.TRIP_ID_ARG])

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
            days = daysWithEvents.map { it.toUiState() }.sortedBy { it.indexInTrip }
        )

    // 扩展函数：DayWithEventsModel -> DayWithEventsUiState
    private fun DayWithEventsModel.toUiState(): DayWithEventsUiState =
        DayWithEventsUiState(
            dayId = id,
            date = date.isoLocalDateToStr(),
            indexInTrip = indexInTrip,
            events = events.map { EventUiState(it.id) }
        )
    //endregion

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
                Log.d("SearchVM", "Request tips for: $keyword")
                val location = if (tripDetailUiState.value is TripDetailUiState.Success)
                    (tripDetailUiState.value as TripDetailUiState.Success).tripLocation
                else ""
                val result = getInputTipsUseCase(keyword, location)
                Log.d("SearchVM", "Result: $result")
                emit(result)
            }.catch { e ->
                Log.e("SearchVM", "Error fetching tips", e)
                emit(emptyList())
            } // 出错显示空列表
        }.map { tipList -> tipList.map { tip->tip.toLocationTipUiState() } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // 当用户输入变化时调用
    fun onKeywordChanged(newKeyword: String)
    {
        Log.d("SearchVM", "Keyword changed: $newKeyword")
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
    val location: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val description: String = ""
)

data class LocationTipUiState(
    val tipId: String = "",
    val name: String = "",
    val address: String = ""
)
