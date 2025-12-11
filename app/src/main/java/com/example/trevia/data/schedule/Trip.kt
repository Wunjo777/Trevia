package com.example.trevia.data.schedule

import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.leancloud.LCObject
import com.example.trevia.domain.schedule.model.TripModel
import com.example.trevia.utils.strToIsoLocalDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true)
    val id: Long=0,
    val name: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val lcObjectId:String?=null
)

fun Trip.toTripModel(): TripModel
{
    return TripModel(
        id = this.id,
        name = this.name,
        destination = this.destination,
        startDate = this.startDate.strToIsoLocalDate(),
        endDate = this.endDate.strToIsoLocalDate(),
        lcObjectId = this.lcObjectId
    )
}


