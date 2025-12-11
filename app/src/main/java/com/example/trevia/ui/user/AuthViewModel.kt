package com.example.trevia.ui.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trevia.domain.login.usecase.LoginUseCase
import com.example.trevia.domain.login.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    // ---------------- UI State ----------------
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    // ---------------- One-shot Events ----------------
    private val _event = MutableSharedFlow<AuthEvent>()
    val event = _event.asSharedFlow()


    // ============ 输入框更新（UI → ViewModel） =============

    fun onUsernameChange(newValue: String) {
        _uiState.update { it.copy(username = newValue) }
    }

    fun onPasswordChange(newValue: String) {
        _uiState.update { it.copy(password = newValue) }
    }

    fun onEmailChange(newValue: String) {
        _uiState.update { it.copy(email = newValue) }
    }


    // ============ 登录 =============
    fun login() {
        val username = uiState.value.username
        val password = uiState.value.password

        if (username.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMsg = "用户名或密码不能为空") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMsg = null) }

            try {
                loginUseCase(username, password)
                _uiState.update { it.copy(isLoading = false) }
                emitEvent(AuthEvent.LoginSuccess)
                Log.d("test", "LoginSuccess emitted")
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                emitEvent(AuthEvent.Error("登录失败：${e.message}"))
                Log.d("test", "LoginError: ${e.message}")
            }
        }
    }


    // ============ 注册 =============
    fun register() {
        val username = uiState.value.username
        val password = uiState.value.password
        val email = uiState.value.email

        if (username.isBlank() || password.isBlank() || email.isBlank()) {
            _uiState.update { it.copy(errorMsg = "请填写全部字段") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMsg = null) }

            try {
                registerUseCase(username, password, email)

                _uiState.update { it.copy(isLoading = false) }
                emitEvent(AuthEvent.RegisterSuccess)

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                emitEvent(AuthEvent.Error("注册失败：${e.message}"))
            }
        }
    }


    // ============ 工具 =============
    private fun emitEvent(event: AuthEvent) {
        viewModelScope.launch { _event.emit(event) }
    }
}

/* ----------------- UI STATE ------------------ */
data class AuthUiState(
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMsg: String? = null
)


/* ----------------- ONE-SHOT EVENTS ------------------ */
sealed class AuthEvent {
    object LoginSuccess : AuthEvent()
    object RegisterSuccess : AuthEvent()
    data class Error(val message: String) : AuthEvent()
}
