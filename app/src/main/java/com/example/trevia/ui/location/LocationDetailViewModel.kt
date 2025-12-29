package com.example.trevia.ui.location

import android.R
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trevia.domain.amap.model.PoiDetailModel
import com.example.trevia.domain.amap.model.WeatherModel
import com.example.trevia.domain.imgupload.usecase.CreateLargeImgUseCase
import com.example.trevia.domain.imgupload.usecase.UploadImgToServerUseCase
import com.example.trevia.domain.location.LocationDetailOrchestratorUseCase
import com.example.trevia.domain.location.UploadLocationCommentUseCase
import com.example.trevia.domain.location.UploadLocationImgMetaUseCase
import com.example.trevia.domain.location.model.DegradeReason
import com.example.trevia.domain.location.model.DomainFailure
import com.example.trevia.domain.location.model.ModuleState
import com.example.trevia.domain.location.model.PoiDecision
import com.example.trevia.domain.location.model.PoiTextLevel
import com.example.trevia.ui.navigation.LocationDetailDestination
import com.example.trevia.work.TaskScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
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

    @RequiresApi(Build.VERSION_CODES.Q)
    fun onImageSelected(uris: List<Uri>)
    {
        val compressQuality = 80
        val maxSize = 1280
        val fileName = "location_$location.jpg"
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
            try
            {
                val results = orchestrator.loadModules(poiId)
                _uiState.value = _uiState.value.copy(
                    poiState = results.poi
                    weatherState = results.weather
                )
            } catch (e: Exception)
            {
                ModuleState.Error(
                    DomainFailure(
                        code = -1,
                        message = e.message ?: "未知错误"
                    )
                )
            }
        }
    }

}

data class LocationDetailUiState(
    var poiState: ModuleState<PoiDetailUiState> = ModuleState.Loading,
    var weatherState: ModuleState<WeatherUiState> = ModuleState.Loading
)


data class PoiDetailUiState
    (
    val poi: PoiDetailModel?,
    val showPoiInfo: Boolean,
    val poiTextLevel: PoiTextLevel,
    val degradeReason: DegradeReason? = null
)

data class WeatherUiState
    (
    val weather: WeatherModel?,
    val showWeather: Boolean,
    val degradeReason: DegradeReason? = null
)


