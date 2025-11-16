package com.example.trevia.data.schedule

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "days")
data class Day(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
