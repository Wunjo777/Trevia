package com.example.trevia.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.trevia.ui.navigation.TreviaNavHost
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.trevia.ui.location.LocationDetailScreen
import com.example.trevia.ui.navigation.AuthNavHost
import com.example.trevia.ui.navigation.CommonDestination

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun TreviaApp(
    navController: NavHostController = rememberNavController(),
    appViewModel: AppViewModel = hiltViewModel()
)
{
    val startDestination = CommonDestination.Schedule
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }
    val isLoggedIn by appViewModel.isLoggedIn.collectAsState()

    val bottomDestinations = listOf(
        CommonDestination.Schedule,
        CommonDestination.Recording,
        CommonDestination.Mine
    )

    Scaffold(
        modifier = Modifier,
        bottomBar = {
            if (isLoggedIn)
            {
                NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                    bottomDestinations.forEachIndexed { index, destination ->
                        NavigationBarItem(
                            selected = selectedDestination == index,
                            onClick = {
                                navController.navigate(destination.route) {
                                    // 避免重复实例
                                    launchSingleTop = true
                                    // 回到 start 目的地，不叠加
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true     // 恢复保存的页面状态
                                    }
                                    restoreState = true       // 切换回来恢复状态
                                }
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
        }
    ) { paddingValues ->
        if (isLoggedIn)
        {
//            LocationDetailScreen()
            TreviaNavHost(navController = navController, modifier = Modifier.padding(paddingValues))
        }
        else
        {
            AuthNavHost(navController = navController, modifier = Modifier.padding(paddingValues))
        }
    }
}
