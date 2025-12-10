package com.example.trevia.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trevia.domain.login.usecase.GetCurrentUserUseCase
import com.example.trevia.domain.login.usecase.LogOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logOutUseCase: LogOutUseCase
) : ViewModel()
{
    companion object
    {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val _logOutUiState = MutableSharedFlow<LogOutEvent>()
    val logOutUiState: SharedFlow<LogOutEvent> = _logOutUiState.asSharedFlow()

    var currentUser: StateFlow<UserProfileUiState> =
        getCurrentUserUseCase().map { userModel ->
            UserProfileUiState(
                username = userModel?.username ?: "",
                email = userModel?.email ?: ""
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            UserProfileUiState()
        )
        private set

    fun logOut()
    {
        viewModelScope.launch {
            try
            {
                logOutUseCase()
                emitEvent(LogOutEvent.Success)
            } catch (e: Exception)
            {
                emitEvent(LogOutEvent.Error("注销失败：${e.message}"))
            }
        }
    }
    private fun emitEvent(event: LogOutEvent) {
        viewModelScope.launch { _logOutUiState.emit(event) }
    }
}

data class UserProfileUiState(
    val username: String = "",
    val email: String = ""
)

sealed class LogOutEvent
{
    object Success : LogOutEvent()
    data class Error(val msg: String) : LogOutEvent()
}
