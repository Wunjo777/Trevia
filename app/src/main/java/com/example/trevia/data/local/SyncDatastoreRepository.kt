package com.example.trevia.data.local

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncDatastoreRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
)
{
    private object Keys
    {
        val TRIP_LAST_SYNC_TIME = longPreferencesKey("trip_last_sync_time")
        val DAY_LAST_SYNC_TIME = longPreferencesKey("day_last_sync_time")
        val EVENT_LAST_SYNC_TIME = longPreferencesKey("event_last_sync_time")
        val PHOTO_LAST_SYNC_TIME = longPreferencesKey("photo_last_sync_time")
    }

    suspend fun getTripLastSyncTime(): Long
    {
        return dataStore.data.catch {
            if (it is IOException)
            {
                Log.e("EEE", "Error reading sync times", it)
                it.printStackTrace()
                emit(emptyPreferences())
            }
            else
            {
                throw it
            }
        }
            .map { syncTimes ->
                syncTimes[Keys.TRIP_LAST_SYNC_TIME] ?: 0L
            }
            .first()
    }

    suspend fun setTripLastSyncTime(time: Long)
    {
        dataStore.edit { syncTimes ->
            syncTimes[Keys.TRIP_LAST_SYNC_TIME] = time
        }
    }

    suspend fun getDayLastSyncTime(): Long
    {
        return dataStore.data.catch {
            if (it is IOException)
            {
                Log.e("EEE", "Error reading sync times", it)
                it.printStackTrace()
                emit(emptyPreferences())
            }
            else
            {
                throw it
            }
        }
            .map { syncTimes ->
                syncTimes[Keys.DAY_LAST_SYNC_TIME] ?: 0L
            }
            .first()
    }

    suspend fun setDayLastSyncTime(time: Long)
    {
        dataStore.edit { syncTimes ->
            syncTimes[Keys.DAY_LAST_SYNC_TIME] = time
        }
    }

    suspend fun getEventLastSyncTime(): Long
    {
        return dataStore.data.catch {
            if (it is IOException)
            {
                Log.e("EEE", "Error reading sync times", it)
                it.printStackTrace()
                emit(emptyPreferences())
            }
            else
            {
                throw it
            }
        }
            .map { syncTimes ->
                syncTimes[Keys.EVENT_LAST_SYNC_TIME] ?: 0L
            }
            .first()
    }

    suspend fun setEventLastSyncTime(time: Long)
    {
        dataStore.edit { syncTimes ->
            syncTimes[Keys.EVENT_LAST_SYNC_TIME] = time
        }
    }

    suspend fun getPhotoLastSyncTime(): Long
    {
        return dataStore.data.catch {
            if (it is IOException)
            {
                Log.e("EEE", "Error reading sync times", it)
                it.printStackTrace()
                emit(emptyPreferences())
            }
            else
            {
                throw it
            }
        }
            .map { syncTimes ->
                syncTimes[Keys.PHOTO_LAST_SYNC_TIME] ?: 0L
            }
            .first()
    }

    suspend fun setPhotoLastSyncTime(time: Long)
    {
        dataStore.edit { syncTimes ->
            syncTimes[Keys.PHOTO_LAST_SYNC_TIME] = time
        }
    }
}