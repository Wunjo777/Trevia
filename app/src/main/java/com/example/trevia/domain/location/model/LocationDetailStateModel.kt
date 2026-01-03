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

data class DomainFailure(val reason: FailureReason, val message: String, val canRetry: Boolean)

data class PoiInputs(
    val poiId: String,            // POI的唯一标识符
    val networkAvailable: Boolean
)

data class WeatherInputs(
    val networkAvailable: Boolean,       // 网络是否可用
    val userPrefShowWeather: Boolean     // 用户是否允许显示天气
)

data class CommentsInput(
    val poiId: String,            // POI的唯一标识符
    val networkAvailable: Boolean,       // 网络是否可用
)

data class MediaInputs(
    val poiId: String,            // POI的唯一标识符
    val location: String,            // 位置信息
    val networkAvailable: Boolean,         // 网络是否可用
    val bandwidthKbps: Int,               // 当前网络带宽 kbps
    val isBatterySaverOn: Boolean,          // 是否开启省电模式
    val userPrefAutoPlayVideo: Boolean,    // 用户是否允许自动播放视频
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

data class CommentsDecision(
    val data: List<CommentModel>,
    val showComments: Boolean,
)

data class MediaDecision(
    val data: MediaModel,
    val showVideo: Boolean,
    val showImage: Boolean,
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
    TIMEOUT,
    DEPENDENCY_UNAVAILABLE
}