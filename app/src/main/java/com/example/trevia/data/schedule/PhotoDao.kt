package com.example.trevia.data.schedule

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

@Dao
interface PhotoDao
{
    @Insert
    suspend fun insert(photo: Photo)

    @Delete
    suspend fun delete(photo: Photo)

    @Update
    suspend fun update(photo: Photo)
}