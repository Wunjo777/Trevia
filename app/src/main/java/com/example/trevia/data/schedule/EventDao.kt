package com.example.trevia.data.schedule

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao
{
    @Insert
    suspend fun insert(event: Event)

    @Delete
    suspend fun delete(event: Event)

    @Update
    suspend fun update(event: Event)

    @Query("SELECT * FROM events WHERE dayId = :dayId")
    fun getEventsByDayId(dayId: Long): Flow<List<Event>>
}