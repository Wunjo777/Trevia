package com.example.trevia.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trevia.domain.login.usecase.GetCurrentUserUseCase
import com.example.trevia.ui.schedule.TripDetail.TripDetailUiState
import com.example.trevia.ui.schedule.TripDetail.TripDetailViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(getCurrentUserUseCase: GetCurrentUserUseCase) : ViewModel()
{
    companion object
    {
        const val TIMEOUT_MILLIS = 5_000L
    }

    var isLoggedIn: StateFlow<Boolean> = getCurrentUserUseCase()
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = false
        )
}