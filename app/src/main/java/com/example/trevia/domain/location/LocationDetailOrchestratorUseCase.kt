package com.example.trevia.domain.location

//import kotlinx.coroutines.supervisorScope
import android.util.Log
import com.example.trevia.data.remote.LocationMediaRepository
import com.example.trevia.data.remote.MediaEnvRepository
import com.example.trevia.data.remote.PoiEnvRepository
import com.example.trevia.data.remote.amap.PoiWeatherRepository
import com.example.trevia.data.remote.leancloud.GetLocationDataRepository
import com.example.trevia.domain.location.model.PoiDetailModel
import com.example.trevia.domain.location.model.WeatherModel
import com.example.trevia.domain.location.decision.DecideCommentUseCase
import com.example.trevia.domain.location.decision.DecideMediaUseCase
import com.example.trevia.domain.location.decision.DecidePoiDataUseCase
import com.example.trevia.domain.location.decision.DecideWeatherUseCase
import com.example.trevia.domain.location.model.CommentInputs
import com.example.trevia.domain.location.model.CommentModel
import com.example.trevia.domain.location.model.DegradeReason
import com.example.trevia.domain.location.model.DomainFailure
import com.example.trevia.domain.location.model.MediaInputs
import com.example.trevia.domain.location.model.MediaModel
import com.example.trevia.domain.location.model.ModuleState
import com.example.trevia.domain.location.model.PoiInputs
import com.example.trevia.domain.location.model.WeatherInputs
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationDetailOrchestratorUseCase @Inject constructor(
    private val poiWeatherRepository: PoiWeatherRepository,
    private val poiEnvRepository: PoiEnvRepository,
    private val getLocationDataRepository: GetLocationDataRepository,
    private val locationMediaRepository: LocationMediaRepository,
    private val mediaEnvRepository: MediaEnvRepository,
    private val decideMediaUseCase: DecideMediaUseCase,
    private val decideCommentUseCase: DecideCommentUseCase,
    private val decidePoiDataUseCase: DecidePoiDataUseCase,
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
                    message = e.message ?: "未知错误"
                )

                LocationDetailModules(
                    poi = ModuleState.Error(failure),
                    weather = ModuleState.Error(failure),
                    comments = ModuleState.Error(failure)
                )
            }
        }

    private suspend fun loadInternal(poiId: String, location: String): LocationDetailModules =
        supervisorScope {

            val poiDeferred = async {
                withTimeoutOrNull(DEFAULT_TIMEOUT_MS) {
                    poiWeatherRepository.getPoiWithWeather(poiId)
                }
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

            val poiRaw = poiDeferred.await()
            val commentRaw = commentDeferred.await()
            val mediaRaw = mediaDeferred.await()

            // ⛔ POI 超时：核心模块失败，整体降级
            if ( poiRaw == null)
            {
                return@supervisorScope LocationDetailModules(
                    poi = ModuleState.Degraded(
                        data = null,
                        reason = DegradeReason.TIMEOUT
                    ),
                    weather = ModuleState.Degraded(
                        data = null,
                        reason = DegradeReason.TIMEOUT
                    ),
                    comments = ModuleState.Degraded(
                        data = null,
                        reason = DegradeReason.TIMEOUT
                    )
                )
            }

            val poiInputs = PoiInputs(
                poiDetail = poiRaw.poi,
                isVisible = poiEnvRepository.isVisible(),
                networkAvailable = poiEnvRepository.isNetworkAvailable()
            )

            val weatherInputs = WeatherInputs(
                weather = poiRaw.weather,
                isVisible = poiEnvRepository.isVisible(),
                networkAvailable = poiEnvRepository.isNetworkAvailable(),
                userPrefShowWeather = true
            )

            val commentInputs = CommentInputs(
                comments = commentRaw,
                isVisible = poiEnvRepository.isVisible(),
                networkAvailable = poiEnvRepository.isNetworkAvailable()
            )

            val mediaInputs = MediaInputs(
                mediaData = mediaRaw,
                isVisible = mediaEnvRepository.isVisible(),
                networkAvailable = mediaEnvRepository.isNetworkAvailable(),
                bandwidthKbps = mediaEnvRepository.estimateBandwidthKbps(),
                isBatterySaverOn = mediaEnvRepository.isBatterySaverOn()
            )

            val poiDecision = decidePoiDataUseCase(poiInputs)
            val weatherDecision = decideWeatherUseCase(weatherInputs)
            val commentDecision = decideCommentUseCase(commentInputs)
            val mediaDecision = decideMediaUseCase(mediaInputs)

            return@supervisorScope LocationDetailModules(
                poi =
                    if (poiDecision.degradeReason == null)
                        ModuleState.Success(poiDecision.poi!!)
                    else
                        ModuleState.Degraded(
                            data = null,
                            reason = poiDecision.degradeReason
                        ),

                weather =
                    if (weatherDecision.degradeReason == null)
                        ModuleState.Success(weatherDecision.weather!!)
                    else
                        ModuleState.Degraded(
                            data = null,
                            reason = weatherDecision.degradeReason
                        ),

                comments =
                    if (commentDecision.degradeReason == null)
                        ModuleState.Success(commentDecision.comments!!)
                    else
                        ModuleState.Degraded(
                            data = null,
                            reason = commentDecision.degradeReason
                        )
            )
        }
}


data class LocationDetailModules(
    val poi: ModuleState<PoiDetailModel>,
    val weather: ModuleState<WeatherModel>,
    val comments: ModuleState<List<CommentModel>>
)
