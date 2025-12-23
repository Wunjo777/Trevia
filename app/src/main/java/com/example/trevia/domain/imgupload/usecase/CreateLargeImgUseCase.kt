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
import java.io.ByteArrayOutputStream
import javax.inject.Singleton

@Singleton
@RequiresApi(Build.VERSION_CODES.Q)
class CreateLargeImgUseCase @Inject constructor(
    @ApplicationContext private val context: Context
)
{

    suspend operator fun invoke(
        uri: Uri,
        compressQuality: Int = 80,
        maxSize: Int = 1280 // 你可以修改为 1280 / 1600 / 2048
    ): ByteArray
    {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        val originalBitmap = ImageDecoder.decodeBitmap(source)

        // 添加降分辨率
        val resizedBitmap = resizeBitmap(originalBitmap, maxSize)

        // 3. 压缩为字节数组
        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, outputStream)
        val bytes = outputStream.toByteArray()
        return bytes
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
