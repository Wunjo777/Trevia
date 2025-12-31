package com.example.trevia.domain.amap.model

import com.example.trevia.domain.location.model.PoiDetailModel
import com.example.trevia.domain.location.model.WeatherModel

data class PoiWithWeatherModel(
    val poi: PoiDetailModel?,
    val weather: WeatherModel?
)
