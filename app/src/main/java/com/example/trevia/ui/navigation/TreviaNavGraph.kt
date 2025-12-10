package com.example.trevia.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.trevia.ui.record.TripRecordDetailScreen
import com.example.trevia.ui.record.TripRecordListScreen
import com.example.trevia.ui.schedule.AddTripScreen
import com.example.trevia.ui.schedule.EditEventScreen
import com.example.trevia.ui.schedule.TripDetail.TripDetailScreen
import com.example.trevia.ui.schedule.TripListScreen
import com.example.trevia.ui.user.MineScreen

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun TreviaNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
)
{
    NavHost(
        navController = navController,
        startDestination = CommonDestination.Schedule.route,
        modifier = modifier
    ) {
        composable(CommonDestination.Mine.route) {
            MineScreen(
                navigateBack = { navController.popBackStack() },
            )
        }
        composable(CommonDestination.Recording.route) {
            TripRecordListScreen(onTripClick = {
                navController.navigate(
                    "${TripRecordDetailDestination.ROUTE}/$it"
                )
            })
        }
        composable(CommonDestination.Schedule.route) {
            TripListScreen(
                navigateToAddTrip = { navController.navigate("add_trip") },
                navigateToTripDetail = { navController.navigate("${TripDetailsDestination.ROUTE}/$it") })
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
            TripDetailScreen(
                navigateBack = { navController.popBackStack() },
                navigateToEditEvent = { navController.navigate("${EditEventDestination.ROUTE}/$it") })
        }
        composable(
            route = TripRecordDetailDestination.routeWithArgs,
            arguments = listOf(navArgument(TripRecordDetailDestination.TRIP_ID_ARG) {
                type = NavType.LongType
            })
        ) { TripRecordDetailScreen(navigateBack = { navController.popBackStack() }) }
        composable(
            route = EditEventDestination.eventWithArgs,
            arguments = listOf(navArgument(EditEventDestination.EVENT_ID_ARG) {
                type = NavType.LongType
            })
        ) {
            EditEventScreen(
                navigateBack = { navController.popBackStack() })
        }
    }
}