package com.example.trevia.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun RegisterScreen(
    vm: LoginViewModel = hiltViewModel(),
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit
)
{
    if (vm.registerSuccess.value)
    {
        LaunchedEffect(Unit) { onRegisterSuccess() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("注册账号", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = vm.username.value,
            onValueChange = { vm.username.value = it },
            label = { Text("账号") }
        )

        OutlinedTextField(
            value = vm.email.value,
            onValueChange = { vm.email.value = it },
            label = { Text("邮箱") }
        )

        OutlinedTextField(
            value = vm.password.value,
            onValueChange = { vm.password.value = it },
            label = { Text("密码") },
            visualTransformation = PasswordVisualTransformation()
        )

        if (vm.errorMsg.value != null)
        {
            Text(vm.errorMsg.value!!, color = Color.Red)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { vm.register() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !vm.isLoading.value
        ) {
            if (vm.isLoading.value)
            {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White
                )
            }
            else
            {
                Text("注册")
            }
        }

        TextButton(onClick = onBack) {
            Text("返回登录")
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun RegisterScreenPreview()
{
    RegisterScreen(
        onRegisterSuccess = {},
        onBack = {}
    )
}
