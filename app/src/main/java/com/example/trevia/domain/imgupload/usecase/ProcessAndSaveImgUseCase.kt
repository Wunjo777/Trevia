package com.example.trevia.domain.imgupload.usecase

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ProcessAndSaveImgUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val generateSquareThumbnailUseCase: GenerateSquareThumbnailUseCase,
    private val saveImgFileUseCase: SaveImgFileUseCase
)
{
    @RequiresApi(Build.VERSION_CODES.Q)
    suspend operator fun invoke(uri: Uri): ProcessAndSaveImgResult
    {
        val uriHash = kotlin.math.abs(uri.toString().hashCode())
        val uuid = java.util.UUID.randomUUID().toString()
        val baseName = "img_${uriHash}_$uuid"
        val thumbFilename = "${baseName}_thumb.jpg"

        //original image
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        val originalBitmap = ImageDecoder.decodeBitmap(source)

        //generate thumbnail and large image
        val thumbBitmap = generateSquareThumbnailUseCase(uri, width = 300)

        //save thumbnail to local storage
        val savedThumbUri = saveImgFileUseCase(thumbBitmap, thumbFilename)

        //save original image to local storage
        val savedLargeUri = saveImgFileUseCase(originalBitmap, baseName, compressQuality = 80)
        return ProcessAndSaveImgResult(
            savedThumbUri = savedThumbUri,
            savedLargeUri = savedLargeUri
        )
    }
}

data class ProcessAndSaveImgResult(
    val savedThumbUri: Uri,
    val savedLargeUri: Uri
)
