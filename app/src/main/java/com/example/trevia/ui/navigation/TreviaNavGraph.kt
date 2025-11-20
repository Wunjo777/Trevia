package com.example.trevia.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.trevia.ui.schedule.AddTripScreen
import com.example.trevia.ui.schedule.TripDetailScreen
import com.example.trevia.ui.schedule.TripListScreen

@Composable
fun TreviaNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
)
{
    NavHost(navController = navController, startDestination = "trip_list") {
        composable("trip_list") {
            TripListScreen(navigateToAddTrip = { navController.navigate("add_trip") })
        }
        composable("add_trip") {
            AddTripScreen(navigateBack = { navController.popBackStack() })
        }
        composable(
            route = TripDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(TripDetailsDestination.TRIP_ID_ARG) {
                type = NavType.LongType
            })
        ) {
            TripDetailScreen()
        }
    }
}