package com.example.trevia

import com.example.trevia.domain.imgupload.usecase.ClassifyPhotoUseCase
import com.example.trevia.domain.imgupload.usecase.DaySummary
import com.example.trevia.domain.imgupload.usecase.EventSummary
import com.example.trevia.domain.imgupload.usecase.ExifData
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.runBlocking

class ClassifyPhotoUseCaseTest
{

    private lateinit var useCase: ClassifyPhotoUseCase
    private val today = LocalDate.of(2023, 1, 1)

    @Before
    fun setUp()
    {
        useCase = ClassifyPhotoUseCase()
    }

    @Test
    fun `returns null when day not found`() = runBlocking {
        val exif = ExifData(dateTaken = LocalDate.of(2022, 12, 31), timeTaken = LocalTime.NOON)
        val days = listOf<DaySummary>()
        val events = listOf<EventSummary>()
        val result = useCase.invoke(exif, days, events)
        assertNull(result)
    }

    @Test
    fun `single time candidate returns its eventId`() = runBlocking {
        val exif = ExifData(dateTaken = today, timeTaken = LocalTime.of(10, 15))
        val day = DaySummary(dayId = 1L, date = today)
        val event = EventSummary(
            eventId = 11L,
            dayId = 1L,
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 0),
            latitude = null,
            longitude = null
        )
        val result = useCase.invoke(exif, listOf(day), listOf(event))
        assertEquals(11L, result)
    }

    @Test
    fun `multiple time candidates without location chooses closest by time midpoint`() =
        runBlocking {
            val exif = ExifData(dateTaken = today, timeTaken = LocalTime.of(11, 20))
            val day = DaySummary(dayId = 2L, date = today)

            val event1 = EventSummary(
                eventId = 21L,
                dayId = 2L,
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                latitude = null,
                longitude = null
            )
            val event2 = EventSummary(
                eventId = 22L,
                dayId = 2L,
                startTime = LocalTime.of(11, 0),
                endTime = LocalTime.of(12, 0),
                latitude = null,
                longitude = null
            )

            val result = useCase.invoke(exif, listOf(day), listOf(event1, event2))
            assertEquals(22L, result) // closer to event2 midpoint (11:30)
        }

    @Test
    fun `multiple time candidates with location chooses nearest by distance`() = runBlocking {
        val exif = ExifData(
            dateTaken = today,
            timeTaken = LocalTime.of(11, 20),
            latitude = 0.0,
            longitude = 0.0
        )
        val day = DaySummary(dayId = 3L, date = today)

        val event1 = EventSummary(
            eventId = 31L,
            dayId = 3L,
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(12, 0),
            latitude = 0.1,
            longitude = 0.1
        )
        val event2 = EventSummary(
            eventId = 32L,
            dayId = 3L,
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(12, 0),
            latitude = 0.001,
            longitude = 0.001
        )

        val result = useCase.invoke(exif, listOf(day), listOf(event1, event2))
        assertEquals(32L, result) // event2 is geographically closer
    }

    @Test
    fun `no time candidates but location candidates chooses nearest within tolerance`() =
        runBlocking {
            val exif =
                ExifData(dateTaken = today, timeTaken = null, latitude = 0.0, longitude = 0.0)
            val day = DaySummary(dayId = 4L, date = today)

            val eventNear = EventSummary(
                eventId = 41L,
                dayId = 4L,
                startTime = null,
                endTime = null,
                latitude = 0.001,
                longitude = 0.0
            )
            val eventFar = EventSummary(
                eventId = 42L,
                dayId = 4L,
                startTime = null,
                endTime = null,
                latitude = 0.002,
                longitude = 0.0
            )

            val result = useCase.invoke(exif, listOf(day), listOf(eventNear, eventFar))
            assertEquals(41L, result)
        }

    @Test
    fun `returns null when no event matches by time or location`() = runBlocking {
        val exif = ExifData(
            dateTaken = today,
            timeTaken = LocalTime.of(5, 0),
            latitude = 10.0,
            longitude = 10.0
        )
        val day = DaySummary(dayId = 5L, date = today)

        val event = EventSummary(
            eventId = 51L,
            dayId = 5L,
            startTime = LocalTime.of(6, 0),
            endTime = LocalTime.of(7, 0),
            latitude = 0.0,
            longitude = 0.0
        )

        val result = useCase.invoke(exif, listOf(day), listOf(event))
        assertNull(result)
    }
}
