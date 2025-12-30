package com.example.trevia.domain.location.model

sealed interface ModuleState<out T> {
    object Loading : ModuleState<Nothing>
    data class Success<T>(val data: T) : ModuleState<T>
    data class Degraded<T>(val data: T?, val reason: DegradeReason) : ModuleState<T>
    data class Error(val failure: DomainFailure) : ModuleState<Nothing>
}

enum class DegradeReason { TIMEOUT, NOT_VISIBLE,UNAVAILABLE,NO_NETWORK,BATTERY_SAVER }
enum class VideoQuality{ SMALL, MEDIUM, LARGE }
data class DomainFailure(val code: Int, val message: String)

data class PoiInputs(
    val poiDetail: PoiDetailModel?,      // POI信息
    val isVisible: Boolean,              // 页面是否可见
    val networkAvailable: Boolean,       // 网络是否可用
)

data class WeatherInputs(
    val weather: WeatherModel?,          // 天气信息
    val isVisible: Boolean,              // 页面是否可见
    val networkAvailable: Boolean,       // 网络是否可用
    val userPrefShowWeather: Boolean     // 用户是否允许显示天气
)

data class CommentInputs(
    val comments: List<CommentModel>?,   // 评论信息
    val isVisible: Boolean,              // 页面是否可见
    val networkAvailable: Boolean,       // 网络是否可用
)

data class MediaInputs(
    val mediaData: MediaModel?,    // 媒体信息
    val isVisible: Boolean,              // 页面是否可见
    val networkAvailable: Boolean,         // 网络是否可用
    val bandwidthKbps:Int,               // 当前网络带宽 kbps
    val isBatterySaverOn:Boolean          // 是否开启省电模式
)

data class PoiDecision(
    val poi: PoiDetailModel?,
    val showPoiInfo: Boolean,
    val degradeReason: DegradeReason? = null
)

data class WeatherDecision(
    val weather: WeatherModel?,
    val showWeather: Boolean,
    val degradeReason: DegradeReason? = null
)

data class CommentDecision(
    val comments: List<CommentModel>?,
    val showComments: Boolean,
    val degradeReason: DegradeReason? = null
)

data class MediaDecision(
    val media: MediaModel?,
    val showMedia: Boolean,
    val autoPlayVideo:Boolean = false,
    val videoQuality: VideoQuality? = null,
    val degradeReason: DegradeReason? = null
)