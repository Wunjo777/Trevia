package com.example.trevia.ui.imgupload

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trevia.domain.imgupload.usecase.GenerateThumbnailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ImgUploadViewModel @Inject constructor(private val generateThumbnailUseCase: GenerateThumbnailUseCase) :
    ViewModel()
{

    private val _imgUploadUiState = MutableStateFlow(ImageUploadUiState())
    val imgUploadUiState: StateFlow<ImageUploadUiState> = _imgUploadUiState.asStateFlow()
    @RequiresApi(Build.VERSION_CODES.Q)
    fun onImageSelected(uris: List<Uri>)
    {
        _imgUploadUiState.value = _imgUploadUiState.value.copy(selectedImageUris = uris)
        uris.forEach { uri ->
            viewModelScope.launch(Dispatchers.IO) {
                val thumbBitmap = generateThumbnailUseCase(uri, Size(300, 300))

                val savedUri = saveBitmapToAppCache(appContext, thumbBitmap)

                _imgUploadUiState.update {
                    it.copy(thumbnailUris = it.thumbnailUris + savedUri)
                }
            }
        }
    }
}

data class ImageUploadUiState(
    val selectedImageUris: List<Uri> = emptyList(),
    val thumbnailUris: List<Uri?> = emptyList(),
    val isUploading: Boolean = false,
    val uploadedUrls: List<String> = emptyList()
)
