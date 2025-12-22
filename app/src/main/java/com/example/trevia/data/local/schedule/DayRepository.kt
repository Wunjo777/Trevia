package com.example.trevia.data.local.schedule

import com.example.trevia.data.remote.SyncState
import com.example.trevia.domain.schedule.model.DayModel
import com.example.trevia.domain.schedule.model.toDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Objects
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DayRepository @Inject constructor(private val dayDao: DayDao)
{
    suspend fun insertDay(dayModel: DayModel) = dayDao.insert(dayModel.toDay())

    suspend fun upsertDays(dayModels: List<DayModel>) = dayDao.upsertDays(dayModels.map { it.toDay() })

    suspend fun getDayIdsByTripId(tripId: Long): List<Long> = dayDao.getDayIdsByTripId(tripId)

    suspend fun getDaysByIds(dayIds: List<Long>): List<DayModel> =
        dayDao.getDaysByIds(dayIds).map { it.toDayModel() }

    suspend fun getDaysBySyncState(syncStates: List<SyncState>): List<DayModel> =
        dayDao.getDaysBySyncState(syncStates).map { it.toDayModel() }

    suspend fun deleteDaysByIds(dayIds: List<Long>) = dayDao.softDeleteDaysByIds(dayIds)

    suspend fun hardDeleteDaysByObjectIds(dayObjects: List<String>) =
        dayDao.hardDeleteDaysByObjectIds(dayObjects)

    suspend fun updateDayWithUpdatedAt(dayId: Long, updatedAt: Long) =
        dayDao.updateDayWithUpdatedAt(dayId, updatedAt)

    suspend fun updateDayWithLcObjectId(dayId: Long, lcObjectId: String) =
        dayDao.updateDayWithLcObjectId(dayId, lcObjectId)

    suspend fun updateDaysWithSynced(dayIds: List<Long>) = dayDao.updateDaysWithSynced(dayIds)

    fun getDaysByTripId(tripId: Long): Flow<List<DayModel>>
    {
        return dayDao.getDaysByTripId(tripId).map { days ->
            days.map { it.toDayModel() }
        }
    }

    suspend fun getDayIdByLcObjectId(dayObjectId: String): Long? =
        dayDao.getDayIdByObjectId(dayObjectId)

    suspend fun getDayMapByObjectIds(dayObjectIds: List<String>): Map<String, DayModel> =
        dayDao.getDaysByObjectIds(dayObjectIds).map { it.toDayModel() }.associateBy { it.lcObjectId!! }
}