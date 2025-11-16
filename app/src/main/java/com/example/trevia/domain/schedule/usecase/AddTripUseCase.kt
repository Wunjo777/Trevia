package com.example.trevia.domain.schedule.usecase

import com.example.trevia.domain.schedule.model.TripModel
import com.example.trevia.domain.schedule.model.isValid

class AddTripUseCase()
{
    suspend operator fun invoke(trip: TripModel)
    {
        try
        {
            if (!trip.isValid())
            {
                throw IllegalArgumentException("行程数据无效")
            }
            // 插入行程到数据库
        }
        catch (e: IllegalArgumentException)
        {
            throw e
        }
    }
}