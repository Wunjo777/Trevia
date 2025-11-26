package com.example.trevia.data.amap

import com.example.trevia.domain.amap.model.TipModel
import jakarta.inject.Inject

class TipRepository @Inject constructor(private val aMapService: AMapService)
{
    suspend fun getInputTips(keyword: String, city: String): List<TipModel>
    {
        return aMapService.getInputTips(keyword, city).map { tip ->
            TipModel(
                tip.poiID,
                tip.name,
                tip.district,
                tip.address
            )
        }
    }
}