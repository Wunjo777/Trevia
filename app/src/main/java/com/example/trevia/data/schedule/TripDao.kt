package com.example.trevia.data.schedule

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao
{
    @Insert
    suspend fun insert(trip: Trip): Long

    @Update
    suspend fun update(trip: Trip)

    @Delete
    suspend fun delete(trip: Trip)

    @Query("SELECT * from trips")
    fun getAllTrips(): Flow<List<Trip>>

    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun deleteTripById(tripId: Long)

    @Query("SELECT * FROM trips WHERE id = :tripId")
    fun getTripById(tripId: Long): Flow<Trip?>
}