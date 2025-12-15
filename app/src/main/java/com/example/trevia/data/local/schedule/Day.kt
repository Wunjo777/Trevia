package com.example.trevia.data.local.schedule

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.trevia.domain.schedule.model.DayModel
import com.example.trevia.utils.strToIsoLocalDate


@Entity(
    tableName = "days",
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,           // 外键对应的父表
            parentColumns = ["id"],           // 父表的主键列
            childColumns = ["tripId"],            // 子表中对应的外键列
            onDelete = ForeignKey.CASCADE         // 当 Trip 删除时，自动删除 Day
        )
    ],
    indices = [Index("tripId")] // 外键列必须加索引
)
data class Day(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tripId: Long,
    val date: String,
    val indexInTrip: Int,
    val lcObjectId:String?=null
)

fun Day.toDayModel(): DayModel
{
    return DayModel(
        id = this.id,
        tripId = this.tripId,
        date = this.date.strToIsoLocalDate(),
        indexInTrip = this.indexInTrip,
        lcObjectId = this.lcObjectId
    )
}
