package com.example.trevia.domain.imgupload.usecase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import androidx.core.graphics.scale

@RequiresApi(Build.VERSION_CODES.Q)
class CreateLargeImgUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val saveImgFileUseCase: SaveImgFileUseCase
)
{

    suspend operator fun invoke(
        uri: Uri,
        fileName: String,
        compressQuality: Int = 80,
        maxSize: Int = 1280 // 你可以修改为 1280 / 1600 / 2048
    ): Uri
    {

        val source = ImageDecoder.createSource(context.contentResolver, uri)
        val originalBitmap = ImageDecoder.decodeBitmap(source)

        // 添加降分辨率
        val resizedBitmap = resizeBitmap(originalBitmap, maxSize)

        // 保存（内部做 compress）
        return saveImgFileUseCase(
            resizedBitmap,
            fileName,
            compressQuality = compressQuality
        )
    }

    // 降分辨率函数
    private fun resizeBitmap(original: Bitmap, maxSize: Int): Bitmap
    {
        val width = original.width
        val height = original.height

        if (width <= maxSize && height <= maxSize)
        {
            return original
        }

        val ratio = if (width > height)
        {
            maxSize / width.toFloat()
        }
        else
        {
            maxSize / height.toFloat()
        }

        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()

        return original.scale(newWidth, newHeight)
    }
}
