package com.example.trevia.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.trevia.ui.navigation.TreviaNavHost
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.trevia.ui.navigation.CommonDestination

@Composable
fun TreviaApp(navController: NavHostController = rememberNavController()) {
    val startDestination = CommonDestination.Schedule
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    val bottomDestinations = listOf(
        CommonDestination.Schedule,
        CommonDestination.Recording,
    )

    Scaffold(
        modifier = Modifier,
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                bottomDestinations.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedDestination == index,
                        onClick = {
                            navController.navigate(route = destination.route)
                            selectedDestination = index
                        },
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.contentDescription
                            )
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { paddingValues ->
        // 将 paddingValues 传给 NavHost，避免被底部栏遮挡
        TreviaNavHost(navController = navController, modifier = Modifier.padding(paddingValues))
    }
}
