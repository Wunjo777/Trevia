package com.example.trevia.data.local.schedule

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DayDao
{
    @Insert
    suspend fun insert(day: Day)

    @Delete
    suspend fun delete(day: Day)

    @Update
    suspend fun update(day: Day)

    @Query("SELECT * FROM days WHERE tripId = :tripId")
    fun getDaysByTripId(tripId: Long): Flow<List<Day>>
}