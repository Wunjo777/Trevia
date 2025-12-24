package com.example.trevia.ui.schedule.TripDetail

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
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
import com.example.trevia.domain.imgupload.model.PhotoModel
import com.example.trevia.domain.imgupload.usecase.AddPhotoUseCase
import com.example.trevia.domain.imgupload.usecase.ClassifyPhotoUseCase
import com.example.trevia.domain.imgupload.usecase.CreateSquareThumbnailUseCase
import com.example.trevia.domain.imgupload.usecase.ParseExifUseCase
import com.example.trevia.domain.schedule.model.EventModel
import com.example.trevia.domain.schedule.usecase.AddEventUseCase
import com.example.trevia.domain.schedule.usecase.DeleteEventByIdUseCase
import com.example.trevia.work.TaskScheduler
import kotlinx.coroutines.Dispatchers
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
import kotlin.collections.forEach
import com.example.trevia.domain.imgupload.usecase.DaySummary
import com.example.trevia.domain.imgupload.usecase.EventSummary
import com.example.trevia.utils.strToIsoLocalDate
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File

@HiltViewModel
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class TripDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    getTripWithDaysAndEventsUseCase: GetTripWithDaysAndEventsUseCase,
    getInputTipsUseCase: GetInputTipsUseCase,
    private val addEventUseCase: AddEventUseCase,
    private val deleteEventByIdUseCase: DeleteEventByIdUseCase,
    private val parseExifUseCase: ParseExifUseCase,
    private val createSquareThumbnailUseCase: CreateSquareThumbnailUseCase,
    private val addPhotoUseCase: AddPhotoUseCase,
    private val classifyPhotoUseCase: ClassifyPhotoUseCase,
    private val taskScheduler: TaskScheduler
) : ViewModel()
{
    companion object
    {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val _currentTripId: Long =
        checkNotNull(savedStateHandle[TripDetailsDestination.TRIP_ID_ARG])

    private val _selectedDayId = MutableStateFlow<Long?>(null)
    val selectedDayId: StateFlow<Long?> = _selectedDayId

    fun onSelectedDayChange(dayId: Long?)
    {
        _selectedDayId.value = dayId
    }

    //region 创建TripDetailUiState
    val tripDetailUiState: StateFlow<TripDetailUiState> =
        getTripWithDaysAndEventsUseCase(_currentTripId)
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
                    it.location,
                    it.address,
                    it.latitude,
                    it.longitude,
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

    fun parseTimeRange(timeRange: String?): Pair<LocalTime?, LocalTime?>
    {
        return timeRange?.split(" ~ ")
            ?.takeIf { it.size == 2 }
            ?.let {
                try
                {
                    Pair(LocalTime.parse(it[0]), LocalTime.parse(it[1]))
                } catch (e: Exception)
                {
                    null
                }
            } ?: Pair(null, null)
    }
    //endregion

    fun addEventByLocation(
        dayId: Long,
        locationName: String,
        address: String,
        latitude: Double,
        longitude: Double,
    )
    {
        val eventModel = EventModel(
            dayId = dayId,
            location = locationName,
            address = address,
            latitude = latitude,
            longitude = longitude,
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

    private val _tips: StateFlow<List<LocationTipUiState>> = _keyword
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

    val tips: StateFlow<List<LocationTipUiState>> = _tips

    // 当用户输入变化时调用
    fun onKeywordChanged(newKeyword: String)
    {
        _keyword.value = newKeyword
    }
    //endregion

    //region 上传图片
    @RequiresApi(Build.VERSION_CODES.Q)
    fun onImageSelected(uris: List<Uri>)
    {
        if (tripDetailUiState.value !is TripDetailUiState.Success) return

        val dayList = (tripDetailUiState.value as TripDetailUiState.Success).days.map {
            DaySummary(
                dayId = it.dayId,
                date = it.date.strToIsoLocalDate()
            )
        }

        val eventList = (tripDetailUiState.value as TripDetailUiState.Success).days.flatMap { day ->
            day.events.map { event ->
                val (start, end) = parseTimeRange(event.timeRange)
                EventSummary(
                    eventId = event.eventId,
                    dayId = day.dayId,
                    startTime = start,
                    endTime = end,
                    latitude = null,      // TODO: 从tips给event添加经纬度
                    longitude = null
                )
            }
        }

        uris.forEach { uri ->
            viewModelScope.launch(Dispatchers.IO) {
                // ---------- 1. 文件名生成 ----------
                val uriHash = kotlin.math.abs(uri.toString().hashCode())
                val uuid = java.util.UUID.randomUUID().toString()
                val baseName = "img_${uriHash}_$uuid"
//                val thumbFilename = "${baseName}_thumb.jpg"
                val largeFilename = "${baseName}_large.jpg"

                val internalFile = File(context.cacheDir, "img_${uuid}.jpg")
                context.contentResolver.openInputStream(uri)!!.use { input ->
                    internalFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                val internalUri=internalFile.toUri()

                //exif元数据解析
                val exifData = parseExifUseCase(internalUri)

                //图片根据exif数据分类
                val classifiedEventId = classifyPhotoUseCase(exifData, dayList, eventList)

                // ---------- 2. 生成缩略图 ----------
//                val savedThumbUri = createSquareThumbnailUseCase(uri, 200, thumbFilename, 80)

                // ---------- 3. 写入数据库（返回 photoId） ----------
                val photoId = addPhotoUseCase(
                    PhotoModel(
                        tripId = _currentTripId,
                        eventId = classifiedEventId,
                        localOriginUri = internalUri.toString()
                    )
                )

                // ---------- 4. 调度创建并上传大图的 Worker （异步） ----------
                taskScheduler.scheduleCreateAndUploadLargeImg(
                    uri = internalUri,
                    photoId = photoId,
                    fileName = largeFilename,
                    compressQuality = 80,
                    maxSize = 1280,
                    thumbnailSize = 200
                )
            }
        }
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
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timeRange: String = "",
    val description: String = ""
)

data class LocationTipUiState(
    val tipId: String = "",
    val name: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
