package com.example.trevia.data.local

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
    }

    suspend fun getLastSyncTime(): Long
    {
        return dataStore.data.catch {
            if (it is IOException)
            {
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

    suspend fun setLastSyncTime(time: Long)
    {
        dataStore.edit { syncTimes ->
            syncTimes[Keys.TRIP_LAST_SYNC_TIME] = time
        }
    }
}