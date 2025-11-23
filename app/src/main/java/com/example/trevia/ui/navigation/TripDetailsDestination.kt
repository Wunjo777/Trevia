package com.example.trevia.ui.navigation

object TripDetailsDestination {
    const val ROUTE = "trip_detail"
    const val TRIP_ID_ARG = "tripId"

    const val routeWithArgs = "$ROUTE/{$TRIP_ID_ARG}"
}