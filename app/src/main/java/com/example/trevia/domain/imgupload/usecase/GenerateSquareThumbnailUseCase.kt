package com.example.trevia.domain.imgupload.usecase

import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.Size
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GenerateSquareThumbnailUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {

    @RequiresApi(Build.VERSION_CODES.Q)
    suspend operator fun invoke(uri: Uri, width: Int): Bitmap {
        return try {
            // Step 1: 使用 loadThumbnail 快速获取小图，避免直接 decode 原图
            val rawBitmap = context.contentResolver.loadThumbnail(uri, Size(width, width), null)

            Log.d(
                "GenerateThumbnailUseCase",
                "Raw thumbnail from system: ${rawBitmap.width}x${rawBitmap.height}"
            )

            // Step 2: extractThumbnail 自动处理缩放+中心裁剪
            // OPTIONS_RECYCLE_INPUT：生成后自动回收 rawBitmap，减少内存占用
            val finalBitmap = ThumbnailUtils.extractThumbnail(
                rawBitmap,
                width,
                width,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT
            )

            Log.d(
                "GenerateThumbnailUseCase",
                "Final square thumbnail: ${finalBitmap.width}x${finalBitmap.height}"
            )

            finalBitmap
        } catch (e: Exception) {
            throw Exception("Failed to generate thumbnail for Uri: $uri", e)
        }
    }
}
