package com.example.trevia.data.local.schedule

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.trevia.data.remote.SyncState
import kotlinx.coroutines.flow.Flow

@Dao
interface DayDao
{
    @Insert
    suspend fun insert(day: Day)

    @Upsert
    suspend fun upsertDays(dayModels: List<Day>)

    @Query("UPDATE days SET syncState = :deleted WHERE id IN (:dayIds)")
    suspend fun softDeleteDaysByIds(dayIds: List<Long>, deleted: SyncState = SyncState.DELETED)

    @Query("DELETE FROM days WHERE lcObjectId IN (:dayObjectIds)")
    suspend fun hardDeleteDaysByObjectIds(dayObjectIds: List<String>)

    @Query("UPDATE days SET updatedAt = :updatedAt WHERE id = :dayId")
    suspend fun updateDayWithUpdatedAt(dayId: Long, updatedAt: Long)

    @Query("UPDATE days SET lcObjectId = :lcObjectId WHERE id = :dayId")
    suspend fun updateDayWithLcObjectId(dayId: Long, lcObjectId: String)

    @Query("UPDATE days SET syncState = :synced WHERE id IN (:dayIds)")
    suspend fun updateDaysWithSynced(dayIds: List<Long>, synced: SyncState = SyncState.SYNCED)

    @Query("SELECT id FROM days WHERE tripId = :tripId")
    fun getDayIdsByTripId(tripId: Long): List<Long>

    @Query("SELECT * FROM days WHERE tripId = :tripId AND syncState != :deleted")
    fun getDaysByTripId(tripId: Long, deleted: SyncState = SyncState.DELETED): Flow<List<Day>>

    @Query("SELECT * FROM days WHERE lcObjectId IN (:dayObjectIds)")
    fun getDaysByObjectIds(dayObjectIds: List<String>): List<Day>

    @Query("SELECT id FROM days WHERE lcObjectId = :dayObjectId")
    suspend fun getDayIdByObjectId(dayObjectId: String): Long?

    @Query("SELECT * FROM days WHERE syncState IN (:syncStates)")
    fun getDaysBySyncState(syncStates: List<SyncState>): List<Day>

    @Query("SELECT * FROM days WHERE id IN (:dayIds)")
    fun getDaysByIds(dayIds: List<Long>): List<Day>
}