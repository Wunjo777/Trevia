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
interface TripDao
{
    @Upsert
    suspend fun upsert(trip: Trip): Long
    @Upsert
    suspend fun upsertTrips(trips: List<Trip>)
    @Query("DELETE FROM trips WHERE lcObjectId IN (:tripObjectIds)")
    suspend fun hardDeleteTripsByObjectIds(tripObjectIds: List<String>)
    @Query("SELECT * FROM trips WHERE syncState IN (:states)")
    suspend fun getTripsBySyncState(states: List<SyncState>): List<Trip>

    @Query("SELECT * FROM trips WHERE lcObjectId IN (:lcObjectIds)")
    suspend fun getTripsByObjectIds(lcObjectIds: List<String>): List<Trip>

    @Query("SELECT id FROM trips WHERE lcObjectId IN (:lcObjectIds)")
    suspend fun getTripIdsByObjectIds(lcObjectIds: List<String>): List<Long>

    @Query("SELECT * from trips WHERE syncState != :deleted")
    fun getAllTrips(deleted: SyncState = SyncState.DELETED): Flow<List<Trip>>

    @Query("update trips set syncState = :deleted where id = :tripId")
    suspend fun softDeleteTripById(tripId: Long, deleted: SyncState = SyncState.DELETED)

    @Query("SELECT * FROM trips WHERE id = :tripId AND syncState != :deleted")
    fun getTripById(tripId: Long, deleted: SyncState = SyncState.DELETED): Flow<Trip?>

    @Query("SELECT * FROM trips WHERE id IN (:tripIds)")
    suspend fun getTripsByIds(tripIds: List<Long>): List<Trip>

    @Query("SELECT id FROM trips WHERE lcObjectId = :lcObjectId")
    suspend fun getTripIdByLcObjectId(lcObjectId: String): Long?

    @Query("update trips set syncState = :synced where id IN (:tripIds)")
    suspend fun updateTripsWithSynced(tripIds: List<Long>, synced: SyncState = SyncState.SYNCED)

    @Query("update trips set lcObjectId = :lcObjectId where id = :id")
    suspend fun updateTripWithLcObjectId(
        id: Long,
        lcObjectId: String
    )

    @Query("update trips set updatedAt = :updatedAt where id = :tripId")
    suspend fun updateTripWithUpdatedAt(tripId: Long, updatedAt: Long)
}