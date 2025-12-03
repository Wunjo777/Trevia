package com.example.trevia.domain.imgupload.usecase

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import java.io.IOException

class SaveImgFileUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
)
{
    suspend operator fun invoke(bitmap: Bitmap, filename: String, compressQuality: Int = 100): Uri =
        withContext(
            Dispatchers.IO
        )
        {
            val dir = File(context.filesDir, "thumbnails")
            if (!dir.exists()) dir.mkdirs()

            val file = File(dir, filename)

            try
            {
                FileOutputStream(file).use { fos ->
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, fos))
                    {
                        throw IOException("Failed to compress bitmap to JPEG for file: ${file.absolutePath}")
                    }
                }
            } catch (e: Exception)
            {
                throw IOException("Failed to save bitmap to file: ${file.absolutePath}", e)
            }

            Uri.fromFile(file)
        }
}