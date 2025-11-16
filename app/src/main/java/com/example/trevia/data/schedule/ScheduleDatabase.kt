package com.example.trevia.data.schedule

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Trip::class, Day::class, Event::class], version = 1, exportSchema = false)
abstract class ScheduleDatabase : RoomDatabase()
{
    abstract fun tripDao(): TripDao

    companion object
    {
        @Volatile
        private var Instance: ScheduleDatabase? = null

        fun getDatabase(context: Context): ScheduleDatabase
        {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    ScheduleDatabase::class.java,
                    "schedule_database"
                ).build()
                    .also { Instance = it }
            }
        }
    }
}