package com.example.trevia.utils

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.Instant

fun Long.toDateString(): String {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(this))
}

fun String.strToIsoLocalDate(): LocalDate
{
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    return LocalDate.parse(this, formatter)
}

fun String.toUtcMillis(): Long =
    Instant.parse(this).toEpochMilli()

fun LocalDate.isoLocalDateToStr(): String
{
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    return this.format(formatter)
}

fun LocalTime.toTimeString(): String
{
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return this.format(formatter)
}

fun String.toLocalTime(): LocalTime
{
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return LocalTime.parse(this, formatter)
}
