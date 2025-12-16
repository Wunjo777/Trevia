package com.example.trevia.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext

private const val SYNC_DATASTORE_NAME = "sync_datastore"

// Application 级唯一 DataStore
val Context.syncDataStore by preferencesDataStore(
    name = SYNC_DATASTORE_NAME
)
