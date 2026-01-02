package com.example.trevia.domain.location

//import kotlinx.coroutines.supervisorScope
import android.util.Log
import com.example.trevia.data.remote.LocationMediaRepository
import com.example.trevia.data.remote.MediaEnvRepository
import com.example.trevia.data.local.LocationDetailEnvRepository
import com.example.trevia.data.remote.leancloud.GetLocationDataRepository
import com.example.trevia.domain.location.model.PoiDetailModel
import com.example.trevia.domain.location.model.WeatherModel
import com.example.trevia.domain.location.decision.DecideCommentUseCase
import com.example.trevia.domain.location.decision.DecideMediaUseCase
import com.example.trevia.domain.location.decision.DecidePoiWeatherUseCase
import com.example.trevia.domain.location.decision.DecideWeatherUseCase
import com.example.trevia.domain.location.model.CommentDecision
import com.example.trevia.domain.location.model.CommentInputs
import com.example.trevia.domain.location.model.CommentModel
import com.example.trevia.domain.location.model.DomainFailure
import com.example.trevia.domain.location.model.MediaDecision
import com.example.trevia.domain.location.model.MediaInputs
import com.example.trevia.domain.location.model.MediaModel
import com.example.trevia.domain.location.model.ModuleState
import com.example.trevia.domain.location.model.PoiDecision
import com.example.trevia.domain.location.model.PoiInputs
import com.example.trevia.domain.location.model.WeatherDecision
import com.example.trevia.domain.location.model.WeatherInputs
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationDetailOrchestratorUseCase @Inject constructor(
    private val locationDetailEnvRepository: LocationDetailEnvRepository,
    private val getLocationDataRepository: GetLocationDataRepository,
    private val locationMediaRepository: LocationMediaRepository,
    private val mediaEnvRepository: MediaEnvRepository,
    private val decideMediaUseCase: DecideMediaUseCase,
    private val decideCommentUseCase: DecideCommentUseCase,
    private val decidePoiWeatherUseCase: DecidePoiWeatherUseCase,
    private val decideWeatherUseCase: DecideWeatherUseCase
)
{
    companion object
    {
        private const val DEFAULT_TIMEOUT_MS: Long = 3000
    }

    suspend fun loadModules(poiId: String, location: String): LocationDetailModules =
        supervisorScope {
            try
            {
                loadInternal(poiId, location)
            } catch (e: Exception)
            {
                Log.e("EEE", "LocationDetailOrchestratorUseCase loadModules error", e)
                val failure = DomainFailure(
                    code = -1,
                    message = e.message ?: "未知错误",
                    canRetry = false
                )
                LocationDetailModules(
                    poi = PoiModule(
                        moduleState = ModuleState.Error(failure),
                        decision = null
                    ),
                    weather = WeatherModule(
                        moduleState = ModuleState.Error(failure),
                        decision = null
                    ),
                    comments = CommentModule(
                        moduleState = ModuleState.Error(failure),
                        decision = null
                    ),
                    media = MediaModule(
                        moduleState = ModuleState.Error(failure),
                        decision = null
                    )
                )

            }
        }

    private suspend fun loadInternal(poiId: String, location: String): LocationDetailModules =
        supervisorScope {
            val isNetworkAvailable = locationDetailEnvRepository.isNetworkAvailable()
            //并行获取raw data
            val poiWeatherDeferred = async {
                val poiInputs = PoiInputs(
                    poiId = poiId,
                    networkAvailable = isNetworkAvailable
                )

                val weatherInputs = WeatherInputs(
                    networkAvailable = isNetworkAvailable,
                    userPrefShowWeather = true //TODO：从datastore获取
                )

               return@async decidePoiWeatherUseCase(poiInputs,weatherInputs)
            }

            val commentDeferred = async {
                withTimeoutOrNull(DEFAULT_TIMEOUT_MS) {
                    getLocationDataRepository.getLocationComment(poiId)
                }
            }

            val mediaDeferred = async {
                withTimeoutOrNull(DEFAULT_TIMEOUT_MS) {
                    val videoUrls = locationMediaRepository.getFirstVideoUrl(keyword = location)
                    val userImgUrls = getLocationDataRepository.getLocationImgUrls(poiId)
                    val webImgUrls =
                        locationMediaRepository.getFirstNImageUrls(keyword = location, count = 3)
                    MediaModel(
                        videoUrlSmall = videoUrls?.small,
                        videoUrlMedium = videoUrls?.medium,
                        videoUrlLarge = videoUrls?.large,
                        imgUrls = userImgUrls + webImgUrls,
                    )
                }
            }

            val awaitResult = poiWeatherDeferred.await()
            val poiRaw = awaitResult.first
            val weatherInputs = awaitResult.second
            val commentRaw = commentDeferred.await()
            val mediaRaw = mediaDeferred.await()

            /* ---------- 映射为 ModuleState（事实层） ---------- */

            val poiState: ModuleState<PoiDetailModel> =
                when
                {
                    poiRaw == null     -> ModuleState.Error(
                        DomainFailure(
                            code = -2,
                            message = "POI 数据加载超时",
                            canRetry = true
                        )
                    )

                    poiRaw.poi == null -> ModuleState.Empty
                    else               -> ModuleState.Success(poiRaw.poi)
                }

            val weatherState: ModuleState<WeatherModel> =
                when
                {
                    poiRaw == null         ->
                        ModuleState.Error(
                            DomainFailure(-3, "天气依赖 POI，不可用", false)
                        )

                    poiRaw.weather == null ->
                        ModuleState.Empty

                    else                   ->
                        ModuleState.Success(poiRaw.weather)
                }

            val commentState: ModuleState<List<CommentModel>> =
                when
                {
                    commentRaw == null   ->
                        ModuleState.Error(
                            DomainFailure(-4, "评论加载超时", true)
                        )

                    commentRaw.isEmpty() ->
                        ModuleState.Empty

                    else                 ->
                        ModuleState.Success(commentRaw)
                }

            val mediaState: ModuleState<MediaModel> =
                when
                {
                    mediaRaw == null                                                                                                                  ->
                        ModuleState.Error(
                            DomainFailure(-5, "媒体加载超时", true)
                        )

                    mediaRaw.videoUrlSmall == null && mediaRaw.videoUrlMedium == null && mediaRaw.videoUrlLarge == null && mediaRaw.imgUrls.isEmpty() ->
                        ModuleState.Empty

                    else                                                                                                                              ->
                        ModuleState.Success(mediaRaw)
                }

            var poiDecision: PoiDecision? = null
            var weatherDecision: WeatherDecision? = null
            var commentDecision: CommentDecision? = null
            var mediaDecision: MediaDecision? = null

            if (poiState is ModuleState.Success)
            {

            }

            if (weatherState is ModuleState.Success)
            {

                weatherDecision = decideWeatherUseCase(weatherInputs)
            }

            if (commentState is ModuleState.Success)
            {
                val commentInputs = CommentInputs(
                    comments = commentRaw,
                    isVisible = poiEnvRepository.isVisible(),
                    networkAvailable = poiEnvRepository.isNetworkAvailable()
                )
                commentDecision = decideCommentUseCase(commentInputs)
            }

            if (mediaState is ModuleState.Success)
            {
                val mediaInputs = MediaInputs(
                    mediaData = mediaRaw,
                    isVisible = mediaEnvRepository.isVisible(),
                    networkAvailable = mediaEnvRepository.isNetworkAvailable(),
                    bandwidthKbps = mediaEnvRepository.estimateBandwidthKbps(),
                    isBatterySaverOn = mediaEnvRepository.isBatterySaverOn()
                )
                mediaDecision = decideMediaUseCase(mediaInputs)
            }
            return@supervisorScope LocationDetailModules(
                poi = PoiModule(
                    moduleState = poiState,
                    decision = poiDecision
                ),
                weather = WeatherModule(
                    moduleState = weatherState,
                    decision = weatherDecision
                ),
                comments = CommentModule(
                    moduleState = commentState,
                    decision = commentDecision
                ),
                media = MediaModule(
                    moduleState = mediaState,
                    decision = mediaDecision
                )
            )
        }
}

data class LocationDetailModules(
    val poi: PoiModule,
    val weather: WeatherModule,
    val comments: CommentModule,
    val media: MediaModule
)

data class PoiModule(
    val moduleState: ModuleState<PoiDetailModel>,
    val decision: PoiDecision?
)

data class WeatherModule(
    val moduleState: ModuleState<WeatherModel>,
    val decision: WeatherDecision?
)

data class CommentModule(
    val moduleState: ModuleState<List<CommentModel>>,
    val decision: CommentDecision?
)

data class MediaModule(
    val moduleState: ModuleState<MediaModel>,
    val decision: MediaDecision?
)


