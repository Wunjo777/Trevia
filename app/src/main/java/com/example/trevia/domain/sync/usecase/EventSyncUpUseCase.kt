package com.example.trevia.domain.sync.usecase

import android.util.Log
import com.example.trevia.data.local.schedule.DayRepository
import com.example.trevia.data.local.schedule.EventRepository
import com.example.trevia.data.remote.SyncState
import com.example.trevia.data.remote.leancloud.SyncEventRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventSyncUpUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val dayRepository: DayRepository,
    private val syncEventRepository: SyncEventRepository
)
{
    suspend operator fun invoke()
    {
        val events =
            eventRepository.getEventsBySyncState(listOf(SyncState.PENDING, SyncState.DELETED))
        if (events.isEmpty())
        {
            return
        }

        val dayIds = events.map { it.dayId }.distinct()
        val days = dayRepository.getDaysByIds(dayIds)
        val dayMap = days.associateBy { it.id }

        val syncableEvents = events.filter { event ->
            val day = dayMap[event.dayId]
            day != null && day.syncState == SyncState.SYNCED//是synced一定有objectId，反之不成立
        }

        val upserts = syncableEvents.filter { it.syncState == SyncState.PENDING }
        val deletes = syncableEvents.filter { it.syncState == SyncState.DELETED }

        val lcObjectIdUpdates = mutableMapOf<Long, String>()

        if (deletes.isNotEmpty())
        {
            val idMap = syncEventRepository.softDeleteEvents(deletes)
            lcObjectIdUpdates.putAll(idMap)//在未上传到服务器之前就将本地的数据删除会产生问题，因此删除时也应获取objectId更新本地
//                TODO("hard delete events on LC after 7 days")
        }

        if (upserts.isNotEmpty())
        {
            val uploadResult = syncEventRepository.upsertEvents(upserts.map {
                Pair(dayMap[it.dayId]!!.lcObjectId!!, it)
            })
            val idMap = uploadResult.dataIdToLcObjectId
            val updatedAtList = uploadResult.updatedAtList

            lcObjectIdUpdates.putAll(idMap)

            updatedAtList.forEachIndexed { index, updatedAt ->
                eventRepository.updateEventWithUpdatedAt(upserts[index].id, updatedAt)
            }
        }

        if (lcObjectIdUpdates.isNotEmpty())
        {
            lcObjectIdUpdates.forEach { (eventId, lcObjectId) ->
                eventRepository.updateEventWithLcObjectId(eventId, lcObjectId)
            }
        }
        //所有数据上传完成后，将本地数据标记为已上传
        eventRepository.updateEventsWithSynced(events.map { it.id })
    }
}