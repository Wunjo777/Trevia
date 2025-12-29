package com.example.trevia.domain.location.decision

import com.example.trevia.domain.location.model.LocationInputs
import com.example.trevia.domain.location.model.PoiDecision
import javax.inject.Inject
import javax.inject.Singleton
import com.example.trevia.domain.location.model.PoiTextLevel
import com.example.trevia.domain.location.model.DegradeReason

@Singleton
class DecidePoiDataUseCase @Inject constructor() {

    suspend operator fun invoke(input: LocationInputs): PoiDecision {

        // 页面不可见
        if (!input.isVisible) {
            return PoiDecision(
                poi = null,
                weather = null,
                showPoiInfo = false,
                showWeather = false,
                poiTextLevel = PoiTextLevel.MINIMAL,
                degradeReason = DegradeReason.NOT_VISIBLE
            )
        }

        // POI 缺失
        if (input.poiDetail == null) {
            return PoiDecision(
                poi = null,
                weather = null,
                showPoiInfo = false,
                showWeather = false,
                poiTextLevel = PoiTextLevel.MINIMAL,
                degradeReason = DegradeReason.POI_UNAVAILABLE
            )
        }

        val showWeather =
            input.userPrefShowWeather &&
                    input.weather != null &&
                    input.networkAvailable

        val poiTextLevel =
            if (!input.networkAvailable) PoiTextLevel.BASIC
            else PoiTextLevel.FULL

        return PoiDecision(
            poi = input.poiDetail,
            weather = if (showWeather) input.weather else null,
            showPoiInfo = true,
            showWeather = showWeather,
            poiTextLevel = poiTextLevel,
            degradeReason = null
        )
    }
}
