package com.example.trevia.domain.imgupload.usecase

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GenerateThumbnailUseCase @Inject constructor(@ApplicationContext private val context: Context) {

    @RequiresApi(Build.VERSION_CODES.Q)
    suspend operator fun invoke(uri: Uri, size: Size): Bitmap?
    {
        val thumbnail: Bitmap =
            context.contentResolver.loadThumbnail(
                uri,
                size, // 你希望的缩略图尺寸
                null
            )
        return thumbnail
    }
}
