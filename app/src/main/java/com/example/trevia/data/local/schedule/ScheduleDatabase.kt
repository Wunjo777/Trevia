package com.example.trevia.data.local.schedule

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.trevia.data.local.cache.PoiCache
import com.example.trevia.data.local.cache.PoiCacheDao
import com.example.trevia.data.local.cache.WeatherCache
import com.example.trevia.data.local.cache.WeatherCacheDao
import javax.inject.Singleton
import com.example.trevia.data.utils.SyncStateConverter
import com.example.trevia.data.local.cache.CommentCache
import com.example.trevia.data.local.cache.CommentCacheDao

@Singleton
@TypeConverters(SyncStateConverter::class)
@Database(entities = [Trip::class, Day::class, Event::class, Photo::class, PoiCache::class, WeatherCache::class, CommentCache::class], version = 18, exportSchema = false)
abstract class ScheduleDatabase : RoomDatabase()
{
    abstract fun tripDao(): TripDao

    abstract fun dayDao(): DayDao

    abstract fun eventDao(): EventDao

    abstract fun photoDao(): PhotoDao

    abstract fun poiCacheDao(): PoiCacheDao

    abstract fun weatherCacheDao(): WeatherCacheDao

    abstract fun commentCacheDao(): CommentCacheDao
}