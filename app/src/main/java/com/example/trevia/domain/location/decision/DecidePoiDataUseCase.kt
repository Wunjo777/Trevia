package com.example.trevia.domain.location.decision

import com.example.trevia.domain.location.model.DegradeReason
import com.example.trevia.domain.location.model.PoiDecision
import com.example.trevia.domain.location.model.PoiInputs
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DecidePoiDataUseCase @Inject constructor() {

    suspend operator fun invoke(input: PoiInputs): PoiDecision {

        // 页面不可见
        if (!input.isVisible) {
            return PoiDecision(
                poi = null,
                showPoiInfo = false,
                degradeReason = DegradeReason.NOT_VISIBLE
            )
        }

        // POI 缺失
        if (input.poiDetail == null) {
            return PoiDecision(
                poi = null,
                showPoiInfo = false,
                degradeReason = DegradeReason.UNAVAILABLE
            )
        }

        return PoiDecision(
            poi = input.poiDetail,
            showPoiInfo = true,
            degradeReason = null
        )
    }
}
