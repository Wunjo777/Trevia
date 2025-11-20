package com.example.trevia.domain.schedule.usecase

import com.example.trevia.data.schedule.TripRepository
import com.example.trevia.di.OfflineRepo
import com.example.trevia.domain.schedule.model.TripModel
import com.example.trevia.domain.schedule.model.isValid
import com.example.trevia.domain.schedule.model.toTrip
import javax.inject.Inject
import com.example.trevia.R

class AddTripUseCase @Inject constructor(
    @OfflineRepo private val tripRepository: TripRepository,
    private val createDaysForTripUseCase: CreateDaysForTripUseCase
)
{
    suspend operator fun invoke(tripModel: TripModel): AddTripResult
    {
        return try
        {
            if (!tripModel.isValid())
            {
                return AddTripResult.InvalidData
            }
            val trip = tripModel.toTrip()
            // Insert the trip into the database
            val tripId = tripRepository.insertTrip(trip)
            // Create the days for the trip
            createDaysForTripUseCase(tripModel.copy(id = tripId))
            AddTripResult.Success
        } catch (e: Exception)
        {
            AddTripResult.DatabaseError
        }
    }
}

sealed class AddTripResult
{
    object Success : AddTripResult()
    object InvalidData : AddTripResult()
    object DatabaseError : AddTripResult()
}