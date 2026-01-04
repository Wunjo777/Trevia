package com.example.trevia

import com.example.trevia.domain.imgupload.usecase.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.abs
import kotlin.random.Random

/**
 * Offline evaluation for ClassifyPhotoUseCase
 *
 * This is NOT a unit test.
 * It simulates real-world noisy EXIF inputs and evaluates classification quality statistically.
 */
class ClassifyPhotoUseCaseAccuracyTest {

    // ---------------- configuration ----------------

    private val RANDOM_RUNS = 3000
    private val TIME_NOISE_MINUTES = 45L        // camera clock drift
    private val LOCATION_NOISE = 0.005           // ~500m GPS drift

    // ---------------- expected outcome model ----------------

    sealed class ExpectedOutcome {
        data class OneOf(val candidates: Set<Long>) : ExpectedOutcome()
        object ShouldBeNull : ExpectedOutcome()
        object Uncertain : ExpectedOutcome()
    }

    data class Sample(
        val exif: ExifData,
        val expected: ExpectedOutcome
    )

    // ---------------- test entry ----------------

    @Test
    fun offlineAccuracyEvaluation() = runBlocking {
        val useCase = ClassifyPhotoUseCase()

        val date = LocalDate.of(2025, 1, 1)
        val days = listOf(
            DaySummary(1L, date)
        )

        // realistic overlapping events
        val events = listOf(
            EventSummary(
                eventId = 10L,
                dayId = 1L,
                startTime = LocalTime.of(9, 0),
                endTime = LocalTime.of(10, 0),
                latitude = 10.0,
                longitude = 20.0
            ),
            EventSummary(
                eventId = 11L,
                dayId = 1L,
                startTime = LocalTime.of(9, 30),
                endTime = LocalTime.of(11, 0),
                latitude = 10.001,
                longitude = 20.001
            ),
            EventSummary(
                eventId = 12L,
                dayId = 1L,
                startTime = LocalTime.of(15, 0),
                endTime = LocalTime.of(16, 0),
                latitude = null,
                longitude = null
            )
        )

        val stats = Stats()

        repeat(RANDOM_RUNS) {
            val sample = generateRandomSample(date, events)
            val result = useCase.invoke(sample.exif, days, events)
            stats.record(result, sample.expected)
        }

        stats.printReport(RANDOM_RUNS)
    }

    // ---------------- random data generation ----------------

    private fun generateRandomSample(
        date: LocalDate,
        events: List<EventSummary>
    ): Sample {

        val r = Random.nextDouble()

        return when {
            // -------- ideal case (clear signal)
            r < 0.4 -> {
                val event = events.random()
                val mid = midpoint(event)
                Sample(
                    exif = ExifData(
                        dateTaken = date,
                        timeTaken = noisyTime(mid),
                        latitude = noisyLat(event.latitude),
                        longitude = noisyLng(event.longitude)
                    ),
                    expected = ExpectedOutcome.OneOf(setOf(event.eventId))
                )
            }

            // -------- ambiguous overlap
            r < 0.65 -> {
                val e1 = events[0]
                val e2 = events[1]
                val time = noisyTime(LocalTime.of(9, 45))

                Sample(
                    exif = ExifData(
                        dateTaken = date,
                        timeTaken = time,
                        latitude = noisyLat(e2.latitude),
                        longitude = noisyLng(e2.longitude)
                    ),
                    expected = ExpectedOutcome.OneOf(setOf(e1.eventId, e2.eventId))
                )
            }

            // -------- missing EXIF
            r < 0.8 -> {
                Sample(
                    exif = ExifData(
                        dateTaken = date,
                        timeTaken = null,
                        latitude = null,
                        longitude = null
                    ),
                    expected = ExpectedOutcome.ShouldBeNull
                )
            }

            // -------- GPS drift / wrong place
            else -> {
                Sample(
                    exif = ExifData(
                        dateTaken = date,
                        timeTaken = noisyTime(LocalTime.of(12, 0)),
                        latitude = 50.0,
                        longitude = 60.0
                    ),
                    expected = ExpectedOutcome.ShouldBeNull
                )
            }
        }
    }

    // ---------------- helpers ----------------

    private fun midpoint(event: EventSummary): LocalTime {
        val start = event.startTime!!
        val end = event.endTime!!
        return start.plusSeconds(Duration.between(start, end).seconds / 2)
    }

    private fun noisyTime(base: LocalTime): LocalTime =
        base.plusMinutes(Random.nextLong(-TIME_NOISE_MINUTES, TIME_NOISE_MINUTES))

    private fun noisyLat(lat: Double?): Double? =
        lat?.plus(Random.nextDouble(-LOCATION_NOISE, LOCATION_NOISE))

    private fun noisyLng(lng: Double?): Double? =
        lng?.plus(Random.nextDouble(-LOCATION_NOISE, LOCATION_NOISE))

    // ---------------- stats ----------------

    private class Stats {
        var total = 0
        var classified = 0
        var acceptable = 0
        var overConfident = 0
        var correctNull = 0

        fun record(result: Long?, expected: ExpectedOutcome) {
            total++

            if (result != null) classified++

            when (expected) {
                is ExpectedOutcome.OneOf -> {
                    if (result in expected.candidates) acceptable++
                    else if (result != null) overConfident++
                }

                ExpectedOutcome.ShouldBeNull -> {
                    if (result == null) correctNull++
                    else overConfident++
                }

                ExpectedOutcome.Uncertain -> {
                    // intentionally ignored
                }
            }
        }

        fun printReport(runs: Int) {
            println("------ Photo Classification Offline Evaluation ------")
            println("Total samples: $runs")
            println("Coverage: ${percent(classified, runs)}")
            println("Acceptable accuracy: ${percent(acceptable, runs)}")
            println("Correct null: ${percent(correctNull, runs)}")
            println("Over-confident error: ${percent(overConfident, runs)}")
            println("-----------------------------------------------------")
        }

        private fun percent(a: Int, b: Int): String =
            "%.2f%%".format(if (b == 0) 0.0 else a * 100.0 / b)
    }
}
