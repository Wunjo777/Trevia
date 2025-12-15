package com.example.trevia.data.local.schedule

import com.example.trevia.domain.schedule.model.DayModel
import com.example.trevia.domain.schedule.model.toDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DayRepository @Inject constructor(private val dayDao: DayDao)
{
    suspend fun insertDay(dayModel: DayModel) = dayDao.insert(dayModel.toDay())


    suspend fun deleteDay(dayModel: DayModel) = dayDao.delete(dayModel.toDay())


    suspend fun updateDay(dayModel: DayModel) = dayDao.update(dayModel.toDay())

    fun getDaysByTripId(tripId: Long): Flow<List<DayModel>>
    {
        return dayDao.getDaysByTripId(tripId).map { days ->
            days.map { it.toDayModel() }
        }
    }
}