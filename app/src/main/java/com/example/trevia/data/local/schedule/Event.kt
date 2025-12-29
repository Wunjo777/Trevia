package com.example.trevia.data.local.schedule

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.trevia.data.remote.SyncState
import com.example.trevia.domain.schedule.model.EventModel
import com.example.trevia.utils.toLocalTime

@Entity(
    tableName = "events",
    foreignKeys = [
        ForeignKey(
            entity = Day::class,           // 外键对应的父表
            parentColumns = ["id"],           // 父表的主键列
            childColumns = ["dayId"],            // 子表中对应的外键列
            onDelete = ForeignKey.CASCADE         // 当 Day 删除时，自动删除 Event
        ),
    ],
    indices = [Index("dayId")] // 外键列必须加索引
)
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dayId: Long,
    val poiId: String,
    val location: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val startTime: String?,
    val endTime: String?,
    val description: String?,
    val lcObjectId: String?,
    val syncState: SyncState,
    val updatedAt: Long
)

fun Event.toEventModel() = EventModel(
    id = this.id,
    dayId = this.dayId,
    poiId = this.poiId,
    location = this.location,
    address = this.address,
    latitude = this.latitude,
    longitude = this.longitude,
    startTime = this.startTime?.toLocalTime(),
    endTime = this.endTime?.toLocalTime(),
    description = this.description,
    lcObjectId = this.lcObjectId,
    syncState = this.syncState,
    updatedAt = this.updatedAt
)
