package com.example.trevia.data.local.schedule

import com.example.trevia.data.remote.SyncState
import com.example.trevia.domain.schedule.model.EventModel
import com.example.trevia.domain.schedule.model.toEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(private val eventDao: EventDao)
{
    suspend fun insertEvent(eventModel: EventModel) = eventDao.insert(eventModel.toEvent())

    suspend fun upsertEvents(events: List<EventModel>) = eventDao.upsertEvents(events.map { it.toEvent() })

    suspend fun updateEvent(eventModel: EventModel) = eventDao.update(eventModel.toEvent())

    suspend fun updateEventWithUpdatedAt(eventId: Long, updatedAt: Long) = eventDao.updateEventWithUpdatedAt(eventId, updatedAt)

    suspend fun updateEventWithLcObjectId(eventId: Long, lcObjectId: String) = eventDao.updateEventWithLcObjectId(eventId, lcObjectId)

    suspend fun updateEventsWithSynced(eventIds: List<Long>) = eventDao.updateEventsWithSynced(eventIds)

    suspend fun deleteEventById(eventId: Long) = eventDao.softDeleteEventById(eventId)

    suspend fun deleteEventsByDayIds(dayIds: List<Long>) = eventDao.softDeleteEventsByDayIds(dayIds)

    suspend fun hardDeleteEventsByObjectIds(objectIds: List<String>) = eventDao.hardDeleteEventsByObjectIds(objectIds)

    fun getEventsByDayId(dayId: Long): Flow<List<EventModel>>
    {
        return eventDao.getEventsByDayId(dayId).map { events ->
            events.map { it.toEventModel() }
        }
    }

    suspend fun getEventById(eventId: Long): EventModel?
    {
        return eventDao.getEventById(eventId)?.toEventModel()
    }

     suspend fun getEventsBySyncState(syncStates: List<SyncState>): List<EventModel>
    {
        return eventDao.getEventsBySyncState(syncStates).map { it.toEventModel() }
    }

     suspend fun getEventMapByObjectIds(objectIds: List<String>): Map<String, EventModel>
    {
        return eventDao.getEventsByObjectIds(objectIds).map { it.toEventModel() }.associateBy { it.lcObjectId!! }
    }
}