package com.example.trevia.ui.user

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun LoginScreen(
    vm: AuthViewModel = hiltViewModel(),
    onGoRegister: () -> Unit
)
{
    val uiState by vm.uiState.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        vm.event.collect { event ->
            when (event)
            {
                is AuthEvent.RegisterSuccess ->
                {
                    Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show()
                }

                is AuthEvent.Error           ->
                {
                    Toast.makeText(context, "登录失败：${event.message}", Toast.LENGTH_SHORT).show()
                }

                else                         ->
                {
                }
            }
        }
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
            value = uiState.username,
            onValueChange = vm::onUsernameChange,
            label = { Text("用户名") }
        )

        OutlinedTextField(
            value = uiState.password,
            onValueChange = vm::onPasswordChange,
            label = { Text("密码") },
            visualTransformation = PasswordVisualTransformation()
        )

        if (uiState.errorMsg != null)
        {
            Text(
                uiState.errorMsg!!,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { vm.login() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading)
            {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White
                )
            }
            else
            {
                Text("登录")
            }
        }

        TextButton(onClick = onGoRegister) {
            Text("还没有账号？去注册")
        }
    }
}
