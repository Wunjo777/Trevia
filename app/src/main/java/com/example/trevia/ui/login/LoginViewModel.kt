package com.example.trevia.ui.login

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trevia.domain.login.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val registerUseCase: RegisterUseCase) : ViewModel()
{

    var username = mutableStateOf("")
    var password = mutableStateOf("")
    var email = mutableStateOf("")

    var isLoading = mutableStateOf(false)
    var errorMsg = mutableStateOf<String?>(null)
    var loginSuccess = mutableStateOf(false)
    var registerSuccess = mutableStateOf(false)

    fun login()
    {
        if (username.value.isBlank() || password.value.isBlank())
        {
            errorMsg.value = "用户名或密码不能为空"
            return
        }

        isLoading.value = true
        errorMsg.value = null

        LCUser.logIn(username.value, password.value)
            .subscribe({ user ->
                isLoading.value = false
                loginSuccess.value = true
            }, { error ->
                isLoading.value = false
                errorMsg.value = "登录失败：${error.message}"
            })
    }

    fun register()
    {
        if (username.value.isBlank() ||
            password.value.isBlank() ||
            email.value.isBlank()
        )
        {
            errorMsg.value = "请填写全部字段"
            return
        }

        isLoading.value = true
        errorMsg.value = null

        viewModelScope.launch {
            try
            {
                registerUseCase(username.value, password.value, email.value)
                isLoading.value = false
                registerSuccess.value = true
            } catch (e: Exception)
            {
                isLoading.value = false
                errorMsg.value = "注册失败：${e.message}"
            }
        }
    }
}
