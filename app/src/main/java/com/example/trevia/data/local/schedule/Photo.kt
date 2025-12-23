package com.example.trevia.data.local.schedule

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.trevia.data.remote.SyncState
import com.example.trevia.domain.imgupload.model.PhotoModel
import retrofit2.http.Url

@Entity(
    tableName = "photos",
    foreignKeys = [
        ForeignKey(
            entity = Event::class,           // 外键对应的父表
            parentColumns = ["id"],           // 父表的主键列
            childColumns = ["eventId"],            // 子表中对应的外键列
            onDelete = ForeignKey.CASCADE         // 当 Day 删除时，自动删除 Event
        ),
        ForeignKey(
            entity = Trip::class,           // 外键对应的父表
            parentColumns = ["id"],           // 父表的主键列
            childColumns = ["tripId"],            // 子表中对应的外键列
            onDelete = ForeignKey.CASCADE         // 当 Trip 删除时，自动删除 Photo
        ),
    ],
    indices = [Index("eventId"), Index("tripId")] // 外键列必须加索引
)
data class Photo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tripId: Long,
    val eventId: Long?,
    val localOriginUri: String?,
    val largeImgUrl: String?,
    val thumbnailUrl: String?,
    val lcObjectId: String?,
    val syncState: SyncState,
    val updatedAt: Long
)

fun Photo.toPhotoModel(): PhotoModel = PhotoModel(
    id = id,
    tripId = tripId,
    eventId = eventId,
    localOriginUri = localOriginUri,
    largeImgUrl = largeImgUrl,
    thumbnailUrl = thumbnailUrl,
    lcObjectId = lcObjectId,
    syncState = syncState,
    updatedAt = updatedAt
)
