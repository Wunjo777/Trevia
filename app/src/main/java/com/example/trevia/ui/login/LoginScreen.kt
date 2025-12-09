package com.example.trevia.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun LoginScreen(
    vm: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onGoRegister: () -> Unit
) {
    if (vm.loginSuccess.value) {
        LaunchedEffect(Unit) { onLoginSuccess() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("登录", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = vm.username.value,
            onValueChange = { vm.username.value = it },
            label = { Text("用户名") }
        )

        OutlinedTextField(
            value = vm.password.value,
            onValueChange = { vm.password.value = it },
            label = { Text("密码") },
            visualTransformation = PasswordVisualTransformation()
        )

        if (vm.errorMsg.value != null) {
            Text(vm.errorMsg.value!!, color = Color.Red)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { vm.login() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !vm.isLoading.value
        ) {
            if (vm.isLoading.value) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White
                )
            } else {
                Text("登录")
            }
        }

        TextButton(onClick = onGoRegister) {
            Text("还没有账号？去注册")
        }
    }
}
