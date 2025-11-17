package com.example.trevia.data.schedule

interface TripRepository
{
    suspend fun insertTrip(trip: Trip)

    suspend fun deleteTrip(trip: Trip)

    suspend fun updateTrip(trip: Trip)
}