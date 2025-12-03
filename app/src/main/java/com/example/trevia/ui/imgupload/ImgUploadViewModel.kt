package com.example.trevia.ui.imgupload

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trevia.domain.imgupload.model.PhotoModel
import com.example.trevia.domain.imgupload.usecase.AddPhotoUseCase
import com.example.trevia.domain.imgupload.usecase.CreateSquareThumbnailUseCase
import com.example.trevia.domain.imgupload.usecase.GetAllPhotosUseCase
import com.example.trevia.work.TaskScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

@HiltViewModel
class ImgUploadViewModel @Inject constructor(
    private val createSquareThumbnailUseCase: CreateSquareThumbnailUseCase,
    private val addPhotoUseCase: AddPhotoUseCase,
    private val getAllPhotosUseCase: GetAllPhotosUseCase,
    private val taskScheduler: TaskScheduler
) :
    ViewModel()
{
    companion object
    {
        const val TIMEOUT_MILLIS = 5_000L
    }

    @OptIn(FlowPreview::class)
    private val _imgUploadUiState: StateFlow<ImgUploadUiState> =
        getAllPhotosUseCase()
            .debounce(300)
            .map { photoList ->
                ImgUploadUiState(
                    largeImgUris = photoList.map { it.largeImgPath.toUri() },
                    thumbnailUris = photoList.map { it.thumbnailPath.toUri() }
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ImgUploadUiState()
            )

    val imgUploadUiState: StateFlow<ImgUploadUiState> = _imgUploadUiState

    @RequiresApi(Build.VERSION_CODES.Q)
    fun onImageSelected(uris: List<Uri>)
    {
        uris.forEach { uri ->
            viewModelScope.launch(Dispatchers.IO) {
                // ---------- 1. 文件名生成 ----------
                val uriHash = kotlin.math.abs(uri.toString().hashCode())
                val uuid = java.util.UUID.randomUUID().toString()
                val baseName = "img_${uriHash}_$uuid"
                val thumbFilename = "${baseName}_thumb.jpg"
                val largeFilename = "${baseName}_large.jpg"

                // ---------- 2. 生成缩略图 ----------
                val savedThumbUri = createSquareThumbnailUseCase(uri, 200, thumbFilename, 80)

                // ---------- 3. 写入数据库（返回 photoId） ----------
                val photoId = addPhotoUseCase(
                    PhotoModel(
                        thumbnailPath = savedThumbUri.path.toString(),
                        uploadedToServer = false
                    )
                )
                // ---------- 4. 调度创建大图的 Worker （异步） ----------
                taskScheduler.scheduleCreateAndAddLargeImg(
                    uri = uri,
                    photoId = photoId,
                    fileName = largeFilename,
                    compressQuality = 80,
                    maxSize = 1280
                )
            }
        }
    }
}

data class ImgUploadUiState(
    val largeImgUris: List<Uri> = emptyList(),
    val thumbnailUris: List<Uri> = emptyList(),
    val isUploading: Boolean = false,
    val uploadedUrls: List<String> = emptyList()
)
