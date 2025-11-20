package com.example.trevia.data.schedule

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

@Dao
interface DayDao
{
    @Insert
    suspend fun insert(day: Day)

    @Delete
    suspend fun delete(day: Day)

    @Update
    suspend fun update(day: Day)
}