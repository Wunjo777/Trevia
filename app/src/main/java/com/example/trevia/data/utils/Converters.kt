package com.example.trevia.data.utils

import androidx.room.TypeConverter
import com.example.trevia.data.remote.SyncState

class SyncStateConverter {

    @TypeConverter
    fun fromSyncState(state: SyncState): String = state.name

    @TypeConverter
    fun toSyncState(value: String): SyncState = SyncState.valueOf(value)
}
