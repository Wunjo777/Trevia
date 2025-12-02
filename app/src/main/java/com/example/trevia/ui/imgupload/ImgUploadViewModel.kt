package com.example.trevia.ui.imgupload

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trevia.domain.imgupload.model.PhotoModel
import com.example.trevia.domain.imgupload.usecase.AddPhotoUseCase
import com.example.trevia.domain.imgupload.usecase.GenerateSquareThumbnailUseCase
import com.example.trevia.domain.imgupload.usecase.ProcessAndSaveImgUseCase
import com.example.trevia.domain.imgupload.usecase.SaveImgFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ImgUploadViewModel @Inject constructor(
    private val processAndSaveImgUseCase: ProcessAndSaveImgUseCase,
    private val addPhotoUseCase: AddPhotoUseCase
) :
    ViewModel()
{
    private val _imgUploadUiState = MutableStateFlow(ImgUploadUiState())
    val imgUploadUiState: StateFlow<ImgUploadUiState> = _imgUploadUiState.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.Q)
    fun onImageSelected(uris: List<Uri>)
    {
        _imgUploadUiState.value = _imgUploadUiState.value.copy(selectedImageUris = uris)
        val newThumbs = mutableListOf<Uri>()
        uris.forEach { uri ->
            viewModelScope.launch(Dispatchers.IO) {
                //process and save image
                val processAndSaveImgResult = processAndSaveImgUseCase(uri)
                val savedThumbUri = processAndSaveImgResult.savedThumbUri
                val savedLargeUri = processAndSaveImgResult.savedLargeUri

                //add photo to database
                addPhotoUseCase(
                    PhotoModel(
                        largeImgPath = savedLargeUri.path.toString(),
                        thumbnailPath = savedThumbUri.path.toString(),
                        uploadedToServer = false
                    )
                )
                //每5张图片更新一次ui，避免大量ui刷新
                synchronized(newThumbs) {
                    newThumbs.add(savedThumbUri)
                    if (newThumbs.size % 5 == 0 || newThumbs.size == uris.size)
                    {//每5张图更新，最后一次也更新
                        _imgUploadUiState.update {
                            it.copy(thumbnailUris = it.thumbnailUris + newThumbs)
                        }
                    }
                }
            }
        }
    }
}

data class ImgUploadUiState(
    val selectedImageUris: List<Uri> = emptyList(),
    val thumbnailUris: List<Uri> = emptyList(),
    val isUploading: Boolean = false,
    val uploadedUrls: List<String> = emptyList()
)
