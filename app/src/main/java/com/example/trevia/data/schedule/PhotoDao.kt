package com.example.trevia.data.schedule

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao
{
    @Insert
    suspend fun insert(photo: Photo):Long

    @Delete
    suspend fun delete(photo: Photo)

    @Update
    suspend fun update(photo: Photo)

    @Query("UPDATE photos SET largeImgPath = :largeImgPath WHERE id = :id")
    suspend fun updateLargeImgPath(id: Long, largeImgPath: String)

    @Query("SELECT * FROM photos")
    fun getAllPhotos(): Flow<List<Photo>>
}