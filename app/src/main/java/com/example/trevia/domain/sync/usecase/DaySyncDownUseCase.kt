package com.example.trevia.domain.sync.usecase

import com.example.trevia.data.local.SyncDatastoreRepository
import com.example.trevia.data.local.schedule.DayRepository
import com.example.trevia.data.remote.SyncState
import com.example.trevia.data.remote.leancloud.SyncDayRepository
import com.example.trevia.domain.schedule.model.DayModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DaySyncDownUseCase @Inject constructor(
    private val dayRepository: DayRepository,
    private val syncDayRepository: SyncDayRepository,
    private val syncDatastoreRepository: SyncDatastoreRepository
)
{
    suspend operator fun invoke()
    {
        val lastSyncTime = syncDatastoreRepository.getDayLastSyncTime()

        val changedDays = syncDayRepository.getDayModelsAfter(lastSyncTime)

        if (changedDays.isEmpty())
        {
            return
        }

        val upserts = changedDays.filter { it.syncState == SyncState.SYNCED }
        val deletes = changedDays.filter { it.syncState == SyncState.DELETED }

        dayRepository.hardDeleteDaysByObjectIds(deletes.map { it.lcObjectId!! })

        val upsertsDayMap =
            dayRepository.getDayMapByObjectIds(upserts.map { it.lcObjectId!! })

        val daysToUpsert = upserts.mapNotNull { day ->
            val currentDay = upsertsDayMap[day.lcObjectId!!]
            when
            {
                currentDay == null                                                                 -> day//服务端新的，直接插入
                currentDay.updatedAt >= day.updatedAt || currentDay.syncState == SyncState.DELETED -> null//刚上传和本地删除的，不更新
                else                                                                               -> day.copy(id = currentDay.id)
            }
        }

        if (daysToUpsert.isNotEmpty())
        {
            dayRepository.upsertDays(daysToUpsert)
        }

        syncDatastoreRepository.setDayLastSyncTime(changedDays.last().updatedAt)
    }
}