package com.example.trevia.data.schedule

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.trevia.domain.schedule.model.EventModel
import com.example.trevia.domain.schedule.model.toEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EventRepository @Inject constructor(private val eventDao: EventDao)
{
    suspend fun insertEvent(eventModel: EventModel) = eventDao.insert(eventModel.toEvent())

    suspend fun deleteEvent(eventModel: EventModel) = eventDao.delete(eventModel.toEvent())

    suspend fun updateEvent(eventModel: EventModel) = eventDao.update(eventModel.toEvent())

    fun getEventsByDayId(dayId: Long): Flow<List<EventModel>>
    {
        return eventDao.getEventsByDayId(dayId).map { events ->
            events.map { it.toEventModel() }
        }
    }
}