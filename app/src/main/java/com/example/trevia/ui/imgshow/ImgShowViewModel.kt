package com.example.trevia.ui.imgshow

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trevia.domain.imgupload.usecase.GetPhotosByTripIdUseCase
import com.example.trevia.ui.navigation.TripDetailsDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ImgShowViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPhotosByTripIdUseCase: GetPhotosByTripIdUseCase
) : ViewModel()
{
    companion object
    {
        private const val TIMEOUT_MILLIS = 5_000L
    }

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
}

data class ImgShowUiState(val groupedPhotos: Map<Long, List<PhotoUiState>>)

data class PhotoUiState(
    val photoId: Long,
    val eventId: Long?,
    val thumbnailPath: String,
    val largeImgPath: String
)
