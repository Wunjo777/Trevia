package com.example.trevia.di

import android.content.Context
import androidx.room.Room
import com.example.trevia.data.schedule.ScheduleDatabase
import com.example.trevia.data.schedule.TripDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule
{
    @Provides
    @Singleton//Hilt内部会自动保证多线程访问时只创建一个实例
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ScheduleDatabase
    {
        return Room.databaseBuilder(
            context,
            ScheduleDatabase::class.java,
            "schedule_database"
        ).build()
    }

    @Provides//database线程安全，则Dao在room中也是线程安全的
    fun provideTripDao(db: ScheduleDatabase): TripDao
    {
        return db.tripDao()
    }
}