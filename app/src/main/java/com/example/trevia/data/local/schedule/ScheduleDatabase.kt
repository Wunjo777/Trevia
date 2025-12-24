package com.example.trevia.data.local.schedule

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import javax.inject.Singleton
import com.example.trevia.data.utils.SyncStateConverter

@Singleton
@TypeConverters(SyncStateConverter::class)
@Database(entities = [Trip::class, Day::class, Event::class, Photo::class], version = 14, exportSchema = false)
abstract class ScheduleDatabase : RoomDatabase()
{
    abstract fun tripDao(): TripDao

    abstract fun dayDao(): DayDao

    abstract fun eventDao(): EventDao

    abstract fun photoDao(): PhotoDao
}