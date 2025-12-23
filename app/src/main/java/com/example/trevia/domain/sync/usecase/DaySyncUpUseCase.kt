package com.example.trevia.domain.sync.usecase

import android.util.Log
import com.example.trevia.data.local.schedule.DayRepository
import com.example.trevia.data.local.schedule.TripRepository
import com.example.trevia.data.remote.SyncState
import com.example.trevia.data.remote.leancloud.SyncDayRepository
import com.example.trevia.di.OfflineRepo
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DaySyncUpUseCase @Inject constructor(
    private val dayRepository: DayRepository,
    @OfflineRepo private val tripRepository: TripRepository,
    private val syncDayRepository: SyncDayRepository
)
{
    suspend operator fun invoke()
    {
        val days =
            dayRepository.getDaysBySyncState(listOf(SyncState.PENDING, SyncState.DELETED))
        if (days.isEmpty())
        {
            return
        }

        val tripIds = days.map { it.tripId }.distinct()
        val trips = tripRepository.getTripsByIds(tripIds)
        val tripMap = trips.associateBy { it.id }

        val syncableDays = days.filter { day ->
            val trip = tripMap[day.tripId]
            trip != null && trip.syncState == SyncState.SYNCED//是synced一定有objectId，反之不成立
        }

        val upserts = syncableDays.filter { it.syncState == SyncState.PENDING }//新建的
        val deletes = syncableDays.filter { it.syncState == SyncState.DELETED }//删除的
        val lcObjectIdUpdates = mutableMapOf<Long, String>()

        if (deletes.isNotEmpty())
        {
            val idMap = syncDayRepository.softDeleteDays(deletes)
            lcObjectIdUpdates.putAll(idMap)
            //删除不需要指定外键信息
//                TODO("hard delete days on LC after 7 days")
        }

        if (upserts.isNotEmpty())
        {
            val uploadResult =
                syncDayRepository.upsertDays(upserts.map {
                    Pair(
                        tripMap[it.tripId]!!.lcObjectId!!,
                        it
                    )
                })
            val idMap = uploadResult.dataIdToLcObjectId
            val updatedAtList = uploadResult.updatedAtList

            lcObjectIdUpdates.putAll(idMap)

            updatedAtList.forEachIndexed { index, updatedAt ->
                dayRepository.updateDayWithUpdatedAt(upserts[index].id, updatedAt)
            }

        }

        if (lcObjectIdUpdates.isNotEmpty())
        {
            lcObjectIdUpdates.forEach { (dayId, lcObjectId) ->
                dayRepository.updateDayWithLcObjectId(dayId, lcObjectId)
            }
        }
        //所有数据上传完成后，将本地数据标记为已上传
        dayRepository.updateDaysWithSynced(days.map { it.id })
    }
}