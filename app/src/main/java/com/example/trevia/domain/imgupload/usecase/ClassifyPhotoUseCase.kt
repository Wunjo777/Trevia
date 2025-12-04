package com.example.trevia.domain.imgupload.usecase

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.abs
import kotlin.math.*

class ClassifyPhotoUseCase
{
    companion object
    {
        const val TIME_TOL = 30L     // 时间容差（用户到晚/离开早）
        const val DIST_TOL = 0.003   // 经纬度容差（~300m）
        const val DIST_TOL_SQ = DIST_TOL * DIST_TOL
    }

    suspend operator fun invoke(
        exifData: ExifData,
        days: List<DaySummary>,
        events: List<EventSummary>
    ): ImageClassificationResult
    {

        val exifDate = exifData.dateTaken
        val exifTime = exifData.timeTaken
        val exifLat = exifData.latitude
        val exifLng = exifData.longitude

        // ---------- 1. 分类 Day ----------
        val matchedDay = days.firstOrNull { day ->
            exifDate != null && day.date == exifDate
        }
        val dayId = matchedDay?.dayId

        if (dayId == null) return ImageClassificationResult(null, null)

        // ---------- 2. 分类 Event ----------
        val eventsOfDay = events.filter { it.dayId == dayId }

        if (eventsOfDay.isEmpty())
            return ImageClassificationResult(dayId, null)

        // Step 1：时间候选（如果 exifTime 为空，则跳过时间匹配）
        val timeCandidates =
            if (exifTime == null) emptyList()
            else eventsOfDay.filter { event ->
                val start = event.startTime
                val end = event.endTime
                if (start == null || end == null) return@filter false

                val tolerantStart = start.minusMinutes(TIME_TOL)
                val tolerantEnd = end.plusMinutes(TIME_TOL)
                exifTime in tolerantStart..tolerantEnd
            }

        // Step 2：只有一个时间匹配 → 直接返回
        if (timeCandidates.size == 1)
        {
            return ImageClassificationResult(dayId, timeCandidates.first().eventId)
        }

        // Step 3：多个时间匹配 → 用地理位置排序
        if (timeCandidates.size > 1)
        {
            if (exifLat == null || exifLng == null)
            {
                val best = timeCandidates.minByOrNull { event ->
                    val start = event.startTime!!
                    val end = event.endTime!!
                    val mid = start.plusSeconds(Duration.between(start, end).seconds / 2)
                    abs(Duration.between(mid, exifTime).toMinutes())
                }
                return ImageClassificationResult(dayId, best?.eventId)
            }
            val best = timeCandidates.minByOrNull { event ->
                distance(
                    exifLat, exifLng,
                    event.latitude ?: exifLat,
                    event.longitude ?: exifLng
                )
            }
            return ImageClassificationResult(dayId, best?.eventId)
        }

        // Step 4：没有时间候选 → fallback：使用地理位置匹配
        if (exifLat != null && exifLng != null)
        {
            val locationCandidates = eventsOfDay.filter { event ->
                val lat = event.latitude
                val lng = event.longitude
                if (lat == null || lng == null) return@filter false

                val dx = exifLat - lat
                val dy = exifLng - lng
                dx * dx + dy * dy < DIST_TOL_SQ
            }

            if (locationCandidates.isNotEmpty())
            {
                val best = locationCandidates.minByOrNull { event ->
                    distance(
                        exifLat, exifLng,
                        event.latitude!!,
                        event.longitude!!
                    )
                }
                return ImageClassificationResult(dayId, best?.eventId)
            }
        }

        // Step 5：完全无法分类 event，则返回 day 但 event = null
        return ImageClassificationResult(dayId, null)
    }


    /** 简单距离计算（不需特别精确） */
    private fun distance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double
    {
        val dx = lat1 - lat2
        val dy = lng1 - lng2
        return dx * dx + dy * dy
    }
}


// ----------------- 数据模型 ------------------

data class ImageClassificationResult(
    val dayId: Long?,
    val eventId: Long?
)

data class DaySummary(
    val dayId: Long,
    val date: LocalDate
)

data class EventSummary(
    val eventId: Long,
    val dayId: Long,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val latitude: Double?,
    val longitude: Double?
)
