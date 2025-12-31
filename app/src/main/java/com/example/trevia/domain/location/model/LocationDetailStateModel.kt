package com.example.trevia.domain.location.model

//ModuleState: 描述数据的通用加载状态
sealed interface ModuleState<out T>
{
    object Loading : ModuleState<Nothing>
    data class Success<T>(val data: T) : ModuleState<T>
    object Empty : ModuleState<Nothing>
    data class Error(val failure: DomainFailure) : ModuleState<Nothing>
}

enum class VideoQuality
{ SMALL, MEDIUM, LARGE }

enum class DegradeReason{
    UNAVAILABLE,
    NOT_VISIBLE,
    LOW_BANDWIDTH,
    BATTERY_SAVER_ON}
data class DomainFailure(val code: Int, val message: String, val canRetry: Boolean)

data class PoiInputs(
    val networkAvailable: Boolean
)

data class WeatherInputs(
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
    val bandwidthKbps: Int,               // 当前网络带宽 kbps
    val isBatterySaverOn: Boolean          // 是否开启省电模式
)

//Decision职能：告诉UI该如何显示数据
data class PoiDecision(
    val showPoiInfo: Boolean,
    val degradeReason: DegradeReason? = null
)

data class WeatherDecision(
    val showWeather: Boolean,
    val degradeReason: DegradeReason? = null
)

data class CommentDecision(
    val showComments: Boolean,
    val degradeReason: DegradeReason? = null
)

data class MediaDecision(
    val showMedia: Boolean,
    val autoPlayVideo: Boolean = false,
    val videoQuality: VideoQuality? = null,
    val degradeReason: DegradeReason? = null
)