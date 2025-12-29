package com.example.trevia.domain.location.model

import com.example.trevia.domain.amap.model.PoiDetailModel
import com.example.trevia.domain.amap.model.WeatherModel

sealed interface ModuleState<out T> {
    object Loading : ModuleState<Nothing>
    data class Success<T>(val data: T) : ModuleState<T>
    data class Degraded<T>(val data: T?, val reason: DegradeReason) : ModuleState<T>
    data class Error(val failure: DomainFailure) : ModuleState<Nothing>
}

enum class DegradeReason { TIMEOUT, NOT_VISIBLE,POI_UNAVAILABLE }
data class DomainFailure(val code: Int, val message: String)

data class LocationInputs(
    val poiDetail: PoiDetailModel?,      // POI信息
    val weather: WeatherModel?,          // 天气信息
    val isVisible: Boolean,              // 页面是否可见
    val networkAvailable: Boolean,       // 网络是否可用
    val userPrefShowWeather: Boolean     // 用户是否允许显示天气
)

data class PoiDecision(
    val poi: PoiDetailModel?,
    val weather: WeatherModel?,
    val showPoiInfo: Boolean,
    val showWeather: Boolean,
    val poiTextLevel: PoiTextLevel,
    val degradeReason: DegradeReason? = null
)

enum class PoiTextLevel {
    FULL,       // 全量展示
    BASIC,      // 精简展示
    MINIMAL     // 仅名称
}