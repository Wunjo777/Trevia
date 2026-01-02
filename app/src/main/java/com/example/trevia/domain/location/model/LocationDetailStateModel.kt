package com.example.trevia.domain.location.model

//ModuleState: 描述模块的最终ui使用状态
sealed interface ModuleState<out T>
{
    object Loading : ModuleState<Nothing>
    data class Success<T>(val data: T) : ModuleState<T>
    object Empty : ModuleState<Nothing>
    data class Error(val failure: DomainFailure) : ModuleState<Nothing>
}

enum class VideoQuality
{ SMALL, MEDIUM, LARGE }

data class DomainFailure(val code: Int, val message: String, val canRetry: Boolean)

data class PoiInputs(
    val poiId: String,            // POI的唯一标识符
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
    val data: PoiDetailModel,
    val showPoiInfo: Boolean,
)

data class WeatherDecision(
    val data: WeatherModel,
    val showWeather: Boolean,
)

data class CommentDecision(
    val showComments: Boolean,
)

data class MediaDecision(
    val showMedia: Boolean,
    val autoPlayVideo: Boolean = false,
    val videoQuality: VideoQuality? = null,
)

//描述数据获取结果状态
sealed interface LoadResult<out T>
{

    data class Success<T>(val data: T) : LoadResult<T>

    object Empty : LoadResult<Nothing>

    data class Failure(val reason: FailureReason, val throwable: Throwable? = null) :
        LoadResult<Nothing>
}

enum class FailureReason
{
    NO_NETWORK,
    EXCEPTION,
    DEPENDENCY_UNAVAILABLE
}