package com.example.trevia.domain.location

import com.example.trevia.data.local.LocationDetailEnvRepository
import com.example.trevia.domain.location.decision.DecideCommentUseCase
import com.example.trevia.domain.location.decision.DecideMediaUseCase
import com.example.trevia.domain.location.decision.DecidePoiWeatherUseCase
import com.example.trevia.domain.location.model.CommentsDecision
import com.example.trevia.domain.location.model.CommentsInput
import com.example.trevia.domain.location.model.DomainFailure
import com.example.trevia.domain.location.model.FailureReason
import com.example.trevia.domain.location.model.LoadResult
import com.example.trevia.domain.location.model.MediaDecision
import com.example.trevia.domain.location.model.MediaInputs
import com.example.trevia.domain.location.model.ModuleState
import com.example.trevia.domain.location.model.PoiDecision
import com.example.trevia.domain.location.model.PoiInputs
import com.example.trevia.domain.location.model.WeatherDecision
import com.example.trevia.domain.location.model.WeatherInputs
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationDetailOrchestratorUseCase @Inject constructor(
    private val locationDetailEnvRepository: LocationDetailEnvRepository,
    private val decideMediaUseCase: DecideMediaUseCase,
    private val decideCommentUseCase: DecideCommentUseCase,
    private val decidePoiWeatherUseCase: DecidePoiWeatherUseCase,
)
{
    suspend fun loadModules(poiId: String, location: String): LocationDetailModules =
        supervisorScope {
            val isNetworkAvailable = locationDetailEnvRepository.isNetworkAvailable()
            val isBatterySaverOn = locationDetailEnvRepository.isBatterySaverOn()
            val estimateBandwidthKbps = locationDetailEnvRepository.estimateBandwidthKbps()
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
                return@async decidePoiWeatherUseCase(poiInputs, weatherInputs)
            }

            val commentDeferred = async {
                val commentInputs = CommentsInput(
                    poiId = poiId,
                    networkAvailable = isNetworkAvailable
                )
                return@async decideCommentUseCase(commentInputs)
            }

            val mediaDeferred = async {
                val mediaInputs = MediaInputs(
                    poiId = poiId,
                    location = location,
                    networkAvailable = isNetworkAvailable,
                    bandwidthKbps = estimateBandwidthKbps,
                    isBatterySaverOn = isBatterySaverOn,
                    userPrefAutoPlayVideo = true //TODO：从datastore获取
                )
                return@async decideMediaUseCase(mediaInputs)
            }

            val awaitResult = poiWeatherDeferred.await()
            val poiRaw = awaitResult.first
            val weatherRaw = awaitResult.second
            val commentRaw = commentDeferred.await()
            val mediaRaw = mediaDeferred.await()

            /* ---------- 映射为 ModuleState（事实层） ---------- */

            val poiState = poiRaw.toModuleState()
            val weatherState = weatherRaw.toModuleState()
            val commentState = commentRaw.toModuleState()
            val mediaState = mediaRaw.toModuleState()


            LocationDetailModules(
                poi = poiState,
                weather = weatherState,
                comments = commentState,
                media = mediaState
            )
        }
    private fun <T> LoadResult<T>.toModuleState(): ModuleState<T> =
        when (this) {
            is LoadResult.Empty ->
                ModuleState.Empty

            is LoadResult.Success ->
                ModuleState.Success(data)

            is LoadResult.Failure ->
                ModuleState.Error(
                    DomainFailure(
                        reason = reason,
                        message = throwable?.message ?: "未知错误",
                        canRetry = reason == FailureReason.TIMEOUT
                    )
                )
        }

}

data class LocationDetailModules(
    val poi: ModuleState<PoiDecision>,
    val weather: ModuleState<WeatherDecision>,
    val comments: ModuleState<CommentsDecision>,
    val media: ModuleState<MediaDecision>
)


