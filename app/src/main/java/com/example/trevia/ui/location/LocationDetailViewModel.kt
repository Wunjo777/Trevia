package com.example.trevia.ui.location

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trevia.domain.location.model.PoiDetailModel
import com.example.trevia.domain.location.model.WeatherModel
import com.example.trevia.domain.imgupload.usecase.CreateLargeImgUseCase
import com.example.trevia.domain.imgupload.usecase.UploadImgToServerUseCase
import com.example.trevia.domain.location.LocationDetailOrchestratorUseCase
import com.example.trevia.domain.location.UploadLocationCommentUseCase
import com.example.trevia.domain.location.UploadLocationImgMetaUseCase
import com.example.trevia.domain.location.model.CommentModel
import com.example.trevia.domain.location.model.ModuleState
import com.example.trevia.ui.navigation.LocationDetailDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val createLargeImgUseCase: CreateLargeImgUseCase,
    private val uploadImgToServerUseCase: UploadImgToServerUseCase,
    private val uploadLocationImgMetaUseCase: UploadLocationImgMetaUseCase,
    private val uploadLocationCommentUseCase: UploadLocationCommentUseCase,
    private val orchestrator: LocationDetailOrchestratorUseCase
) : ViewModel()
{
    private val poiId: String =
        checkNotNull(savedStateHandle[LocationDetailDestination.POI_ID_ARG])
    private val location: String =
        checkNotNull(savedStateHandle[LocationDetailDestination.LOCATION_NAME_ARG])
    private val address: String =
        checkNotNull(savedStateHandle[LocationDetailDestination.LOCATION_ADDRESS_ARG])
    private val latitude: Double =
        (checkNotNull(savedStateHandle[LocationDetailDestination.LOCATION_LATITUDE_ARG]) as String).toDouble()
    private val longitude: Double =
        (checkNotNull(savedStateHandle[LocationDetailDestination.LOCATION_LONGITUDE_ARG]) as String).toDouble()

    private val _uiState =
        MutableStateFlow(LocationDetailUiState())

    val uiState: StateFlow<LocationDetailUiState> =
        _uiState.asStateFlow()

    init
    {
        load()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun onImageSelected(uris: List<Uri>)
    {
        val compressQuality = 80
        val maxSize = 1280
        val fileName = "location_$poiId.jpg"
        uris.forEach { uri ->
            viewModelScope.launch {
                val imgBytes = createLargeImgUseCase(
                    uri,
                    compressQuality,
                    maxSize
                )

                // upload to server
                val urlPair = uploadImgToServerUseCase(
                    imgBytes,
                    fileName,
                    thumbnailSize = 200
                )

                // upload meta to server
                uploadLocationImgMetaUseCase(
                    poiId = poiId,
                    imgUrl = urlPair.first
                )
            }
        }
    }

    fun onCommentUpload(commentText: String)
    {
        viewModelScope.launch {
            uploadLocationCommentUseCase(poiId, commentText)
        }
    }

    fun load()
    {
        viewModelScope.launch {
            val results = orchestrator.loadModules(poiId,location)
            _uiState.value = _uiState.value.copy(
                poiState = results.poi,
                weatherState = results.weather,
                commentState = results.comments
            )
        }
    }
}

data class LocationDetailUiState(
    val poiState: ModuleState<PoiDetailModel> = ModuleState.Loading,
    val weatherState: ModuleState<WeatherModel> = ModuleState.Loading,
    val commentState:ModuleState<List<CommentModel>> = ModuleState.Loading
)