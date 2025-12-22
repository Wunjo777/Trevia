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
interface EventDao
{
    @Insert
    suspend fun insert(event: Event)

    @Update
    suspend fun update(event: Event)

    @Upsert
    suspend fun upsertEvents(events: List<Event>)

    @Query("UPDATE events SET updatedAt = :updatedAt WHERE id = :eventId")
    suspend fun updateEventWithUpdatedAt(eventId: Long, updatedAt: Long)

    @Query("UPDATE events SET lcObjectId = :lcObjectId WHERE id = :eventId")
    suspend fun updateEventWithLcObjectId(eventId: Long, lcObjectId: String)

    @Query("UPDATE events SET syncState = :synced WHERE id IN (:eventIds)")
    suspend fun updateEventsWithSynced(eventIds: List<Long>, synced: SyncState = SyncState.SYNCED)

    @Query("UPDATE events SET syncState = :deleted WHERE id = :eventId")
    suspend fun softDeleteEventById(eventId: Long, deleted: SyncState = SyncState.DELETED)

    @Query("UPDATE events SET syncState = :deleted WHERE dayId IN (:dayIds)")
    suspend fun softDeleteEventsByDayIds(dayIds: List<Long>, deleted: SyncState = SyncState.DELETED)

    @Query("DELETE FROM events WHERE lcObjectId IN (:objectIds)")
    suspend fun hardDeleteEventsByObjectIds(objectIds: List<String>)

    @Query("SELECT * FROM events WHERE dayId = :dayId")
    fun getEventsByDayId(dayId: Long): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEventById(eventId: Long): Event?

    @Query("SELECT * FROM events WHERE syncState IN (:syncStates)")
    fun getEventsBySyncState(syncStates: List<SyncState>): List<Event>

     @Query("SELECT * FROM events WHERE lcObjectId IN (:objectIds)")
    suspend fun getEventsByObjectIds(objectIds: List<String>): List<Event>
}