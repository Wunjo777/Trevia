package com.example.trevia.data.schedule

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.trevia.domain.schedule.model.TripModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val name: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
)

fun Trip.toTripModel(): TripModel
{
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    return TripModel(
        id = this.id,
        name = this.name,
        destination = this.destination,
        startDate = LocalDate.parse(this.startDate, formatter),
        endDate = LocalDate.parse(this.endDate, formatter),
        days=emptyList()//TODO: add days
    )
}
