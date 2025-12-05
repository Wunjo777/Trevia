package com.example.trevia.data.schedule

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.trevia.domain.imgupload.model.PhotoModel

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
    val largeImgPath: String,
    val thumbnailPath: String,
    val uploadedToServer: Boolean
)

fun Photo.toPhotoModel(): PhotoModel = PhotoModel(
    id = id,
    tripId = tripId,
    eventId = eventId,
    largeImgPath = largeImgPath,
    thumbnailPath = thumbnailPath,
    uploadedToServer = uploadedToServer
)
