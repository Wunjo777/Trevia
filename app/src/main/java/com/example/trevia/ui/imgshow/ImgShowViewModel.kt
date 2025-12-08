package com.example.trevia.ui.imgshow

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trevia.domain.imgupload.usecase.DeletePhotosByIdsUseCase
import com.example.trevia.domain.imgupload.usecase.GetPhotosByTripIdUseCase
import com.example.trevia.domain.imgupload.usecase.MovePhotosToEventUseCase
import com.example.trevia.ui.navigation.TripDetailsDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImgShowViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getPhotosByTripIdUseCase: GetPhotosByTripIdUseCase,
    private val deletePhotosByIdsUseCase: DeletePhotosByIdsUseCase,
    private val movePhotosToEventUseCase: MovePhotosToEventUseCase
) : ViewModel()
{
    companion object
    {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val _imgSelectionState = mutableStateOf(ImgSelectionState())
    val imgSelectionState: State<ImgSelectionState> = _imgSelectionState

    private val _currentTripId: Long =
        checkNotNull(savedStateHandle[TripDetailsDestination.TRIP_ID_ARG])

    private val _imgShowUiState = getPhotosByTripIdUseCase(_currentTripId)
        .map { photos ->
            // 将每个 PhotoModel 转换为 PhotoUiState
            val photoUiStates = photos.map {
                PhotoUiState(
                    photoId = it.id,
                    eventId = it.eventId,
                    thumbnailPath = it.thumbnailPath,
                    largeImgPath = it.largeImgPath
                )
            }
            // 按 eventId 对 PhotoUiState 进行分组,未分类是 -1L
            val groupedPhotos = photoUiStates.groupBy { it.eventId ?: -1L }
            ImgShowUiState(groupedPhotos)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            ImgShowUiState(emptyMap())
        )

    val imgShowUiState: StateFlow<ImgShowUiState> = _imgShowUiState

    fun imgSelectionEventHandler(
        event: ImgSelectionEvent
    )
    {
        val state = _imgSelectionState.value
        _imgSelectionState.value = when (event)
        {

            is ImgSelectionEvent.EnterSelectionModeAndSelect ->
            {
                state.copy(
                    enabled = true,
                    selectedIds = state.selectedIds + event.id
                )
            }

            is ImgSelectionEvent.Toggle                      ->
            {
                if (event.id in state.selectedIds)
                {
                    state.copy(selectedIds = state.selectedIds - event.id)
                }
                else
                {
                    state.copy(selectedIds = state.selectedIds + event.id)
                }
            }

            is ImgSelectionEvent.StartDrag                   ->
            {
                val updatedSelected =
                    if (event.mode == DragMode.ADD) state.selectedIds + event.id
                    else state.selectedIds - event.id

                state.copy(
                    dragMode = event.mode,
                    lastTouchedIndex = event.index,
                    selectedIds = updatedSelected
                )
            }

            is ImgSelectionEvent.DragOver                    ->
            {
                if (event.index == state.lastTouchedIndex) return

                val updatedSelected =
                    if (state.dragMode == DragMode.ADD) state.selectedIds + event.id
                    else state.selectedIds - event.id

                state.copy(
                    selectedIds = updatedSelected,
                    lastTouchedIndex = event.index
                )
            }

            is ImgSelectionEvent.EndDrag                     ->
            {
                state.copy(
                    dragMode = null,
                    lastTouchedIndex = null
                )
            }

            is ImgSelectionEvent.ExitSelection               ->
            {
                state.copy(
                    enabled = false,
                    selectedIds = emptySet(),
                    dragMode = null,
                    lastTouchedIndex = null
                ) // 全部重置
            }
        }
    }

    fun deleteSelectedPhoto()
    {
        viewModelScope.launch {
            deletePhotosByIdsUseCase(_imgSelectionState.value.selectedIds)
        }
    }

    fun moveSelectedPhotosToEvent(eventId: Long)
    {
        viewModelScope.launch {
            movePhotosToEventUseCase(
                photoIds = _imgSelectionState.value.selectedIds,
                eventId = eventId
            )
        }
    }
}

sealed interface ImgSelectionEvent
{
    data class EnterSelectionModeAndSelect(val id: Long) : ImgSelectionEvent
    data class Toggle(val id: Long) : ImgSelectionEvent
    data class StartDrag(val index: Int, val id: Long, val mode: DragMode) : ImgSelectionEvent
    data class DragOver(val index: Int, val id: Long) : ImgSelectionEvent
    object EndDrag : ImgSelectionEvent
    object ExitSelection : ImgSelectionEvent
}

enum class DragMode
{
    ADD,
    REMOVE
}

data class ImgSelectionState(
    val enabled: Boolean = false,              // 是否处于选择模式
    val selectedIds: Set<Long> = emptySet(),   // 当前选中的图片
    val dragMode: DragMode? = null,            // 加选 or 减选
    val lastTouchedIndex: Int? = null          // 用于防止重复处理拖拽中的同一张图
)

data class ImgShowUiState(val groupedPhotos: Map<Long, List<PhotoUiState>>)

data class PhotoUiState(
    val photoId: Long,
    val eventId: Long?,
    val thumbnailPath: String,
    val largeImgPath: String
)
