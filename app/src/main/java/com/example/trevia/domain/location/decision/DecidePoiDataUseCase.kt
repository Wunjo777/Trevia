package com.example.trevia.domain.location.decision

import com.example.trevia.data.remote.amap.PoiWeatherRepository
import com.example.trevia.domain.location.LocationDetailOrchestratorUseCase.Companion.DEFAULT_TIMEOUT_MS
import com.example.trevia.domain.location.model.DegradeReason
import com.example.trevia.domain.location.model.PoiDecision
import com.example.trevia.domain.location.model.PoiInputs
import com.example.trevia.domain.location.model.WeatherInputs
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DecidePoiDataUseCase @Inject constructor(private val poiWeatherRepository: PoiWeatherRepository) {

    suspend operator fun invoke(poiInput: PoiInputs, weatherInput: WeatherInputs): PoiDecision {


        withTimeoutOrNull(DEFAULT_TIMEOUT_MS) {
            poiWeatherRepository.getPoiWithWeather(poiId)
        }

        return PoiDecision(
            showPoiInfo = true,
            degradeReason = null
        )
    }
}
