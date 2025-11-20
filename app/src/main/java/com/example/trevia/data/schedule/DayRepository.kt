package com.example.trevia.data.schedule

import com.example.trevia.data.schedule.DayDao
import com.example.trevia.data.schedule.Day
import javax.inject.Inject

class DayRepository @Inject constructor(private val dayDao: DayDao)
{
    suspend fun insertDay(day: Day) = dayDao.insert(day)


    suspend fun deleteDay(day: Day) = dayDao.delete(day)


    suspend fun updateDay(day: Day) = dayDao.update(day)

}