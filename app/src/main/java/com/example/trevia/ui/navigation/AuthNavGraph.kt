package com.example.trevia.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.trevia.ui.user.LoginScreen
import com.example.trevia.ui.user.RegisterScreen

@Composable
fun AuthNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
)
{
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(
                onGoRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("login") { popUpTo("register") { inclusive = true } }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
