package com.example.trevia.domain.imgupload.usecase

import android.content.Context
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ParseExifUseCase @Inject constructor(
    @ApplicationContext private val context: Context
)
{
    companion object
    {
        private val EXIF_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")
    }

    suspend operator fun invoke(uri: Uri): ExifData
    {
        val input = context.contentResolver.openInputStream(uri) ?: return ExifData()

        val exif = ExifInterface(input)

        // 解析日期时间
        val datetimeRaw = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
        val (date, time) = datetimeRaw?.let { parseExifDateTime(it) } ?: (null to null)

        // 解析纬度经度
        val latLong = exif.latLong

        return ExifData(
            dateTaken = date,
            timeTaken = time,
            latitude = latLong?.get(0),
            longitude = latLong?.get(1)
        )
    }

    private fun parseExifDateTime(raw: String?): Pair<LocalDate?, LocalTime?>
    {
        if (raw.isNullOrBlank()) return null to null

        return try
        {
            val dateTime = LocalDateTime.parse(raw, EXIF_DATETIME_FORMAT)
            dateTime.toLocalDate() to dateTime.toLocalTime()
        } catch (e: Exception)
        {
            null to null // 如果格式异常，返回 null
        }
    }
}

data class ExifData(
    val dateTaken: LocalDate? = null,
    val timeTaken: LocalTime? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)