package com.example.trevia.domain.sync.usecase

import android.util.Log
import com.example.trevia.data.local.SyncDatastoreRepository
import com.example.trevia.data.local.schedule.EventRepository
import com.example.trevia.data.remote.SyncState
import com.example.trevia.data.remote.leancloud.SyncEventRepository
import com.example.trevia.domain.schedule.model.EventModel
import com.example.trevia.domain.schedule.model.TripModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventSyncDownUseCase @Inject constructor(
    private val syncEventRepository: SyncEventRepository,
    private val syncDatastoreRepository: SyncDatastoreRepository,
    private val eventRepository: EventRepository
)
{
    suspend operator fun invoke()
    {
        val lastSyncTime = syncDatastoreRepository.getEventLastSyncTime()

        val changedEvents = syncEventRepository.getEventsAfter(lastSyncTime)

        if (changedEvents.isEmpty())
        {
            return
        }

        val upserts = changedEvents.filter { it.syncState == SyncState.SYNCED }
        val deletes = changedEvents.filter { it.syncState == SyncState.DELETED }

        eventRepository.hardDeleteEventsByObjectIds(deletes.map{it.lcObjectId!!})

        val eventsToArchive = mutableListOf<EventModel>()

        val upsertsEventMap =
            eventRepository.getEventMapByObjectIds(upserts.map { it.lcObjectId!! })

        val eventsToUpsert = upserts.mapNotNull { event ->
            val currentEvent = upsertsEventMap[event.lcObjectId!!]
            when {
                currentEvent == null -> event//服务端新的，直接插入
                currentEvent.updatedAt >= event.updatedAt || currentEvent.syncState == SyncState.DELETED -> null//刚上传和本地删除的，不更新
                else -> {//需要更新
                    if (currentEvent.syncState == SyncState.PENDING) {//本地存在冲突，先归档
                        eventsToArchive.add(currentEvent)
                    }
                    event.copy(id = currentEvent.id)
                }
            }
        }

        if(eventsToUpsert.isNotEmpty())
        {
            eventRepository.upsertEvents(eventsToUpsert)
        }

        if(eventsToArchive.isNotEmpty())
        {
            TODO("archiveEvents")
        }

        syncDatastoreRepository.setEventLastSyncTime(changedEvents.last().updatedAt)
    }
}