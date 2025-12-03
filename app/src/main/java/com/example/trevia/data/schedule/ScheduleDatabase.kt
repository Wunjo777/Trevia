package com.example.trevia.data.schedule

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Trip::class, Day::class, Event::class, Photo::class], version = 8, exportSchema = false)
abstract class ScheduleDatabase : RoomDatabase()
{
    abstract fun tripDao(): TripDao

    abstract fun dayDao(): DayDao

    abstract fun eventDao(): EventDao

    abstract fun photoDao(): PhotoDao

}