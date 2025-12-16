package com.example.trevia.data.local.schedule

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.trevia.data.remote.SyncState
import com.example.trevia.domain.schedule.model.TripModel
import com.example.trevia.utils.strToIsoLocalDate

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val lcObjectId: String? = null,
    val syncState: SyncState = SyncState.PENDING,
    val updatedAt: Long = 0
)

fun Trip.toTripModel(): TripModel
{
    return TripModel(
        id = this.id,
        name = this.name,
        destination = this.destination,
        startDate = this.startDate.strToIsoLocalDate(),
        endDate = this.endDate.strToIsoLocalDate(),
        lcObjectId = this.lcObjectId,
        syncState = this.syncState,
        updatedAt = this.updatedAt
    )
}


