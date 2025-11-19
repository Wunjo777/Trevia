package com.example.trevia.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.trevia.ui.navigation.TreviaNavHost

@Composable
fun TreviaApp(navController: NavHostController = rememberNavController())
{
    TreviaNavHost(navController = navController)
}