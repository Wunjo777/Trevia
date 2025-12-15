package com.example.trevia.data.local.schedule

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.trevia.data.remote.SyncState
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao
{
    @Insert
    suspend fun insert(trip: Trip): Long

    @Update
    suspend fun update(trip: Trip)

    @Update
    suspend fun updateTrips(trips: List<Trip>)

    @Delete
    suspend fun hardDeleteTrips(trips: List<Trip>)

    @Query("SELECT * FROM trips WHERE syncState IN (:states)")
    suspend fun getTripsBySyncState(states: List<SyncState>): List<Trip>

    @Query("SELECT * from trips WHERE syncState != :deleted")
    fun getAllTrips(deleted: SyncState = SyncState.DELETED): Flow<List<Trip>>

    @Query("update trips set syncState = :deleted where id = :tripId")
    suspend fun softDeleteTripById(tripId: Long, deleted: SyncState = SyncState.DELETED)

    @Query("SELECT * FROM trips WHERE id = :tripId AND syncState != :deleted")
    fun getTripById(tripId: Long, deleted: SyncState = SyncState.DELETED): Flow<Trip?>

    @Query("update trips set syncState = :synced where id IN (:tripIds)")
    suspend fun updateTripsWithSynced(tripIds: List<Long>, synced: SyncState = SyncState.SYNCED)

    @Query("update trips set lcObjectId = :lcObjectId where id = :id")
    suspend fun updateTripWithLcObjectId(
        id: Long,
        lcObjectId: String
    )
}