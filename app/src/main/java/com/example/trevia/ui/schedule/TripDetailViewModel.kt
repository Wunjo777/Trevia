package com.example.trevia.ui.schedule

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import jakarta.inject.Inject

class TripDetailViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel()
{
    @OfflineRepo private val tripRepository: TripRepository
}
