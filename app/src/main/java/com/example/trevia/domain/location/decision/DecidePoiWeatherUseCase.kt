package com.example.trevia.domain.location.decision

import com.example.trevia.data.remote.amap.PoiRepository
import com.example.trevia.data.remote.amap.WeatherRepository
import com.example.trevia.domain.location.model.FailureReason
import com.example.trevia.domain.location.model.LoadResult
import com.example.trevia.domain.location.model.PoiDecision
import com.example.trevia.domain.location.model.PoiDetailModel
import com.example.trevia.domain.location.model.PoiInputs
import com.example.trevia.domain.location.model.WeatherDecision
import com.example.trevia.domain.location.model.WeatherInputs
import com.example.trevia.domain.location.model.WeatherModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DecidePoiWeatherUseCase @Inject constructor(
    private val poiRepository: PoiRepository,
    private val weatherRepository: WeatherRepository
)
{
    suspend operator fun invoke(
        poiInput: PoiInputs,
        weatherInput: WeatherInputs
    ): Pair<LoadResult<PoiDecision>, LoadResult<WeatherDecision>>
    {
        val poiResult: LoadResult<PoiDecision> =
            poiRepository.getCachedPoi(poiInput.poiId)?.let {
                LoadResult.Success(
                    PoiDecision(
                        data = it,
                        showPoiInfo = true
                    )
                )
            } ?: run {
                if (!poiInput.networkAvailable)
                {
                    LoadResult.Failure(FailureReason.NO_NETWORK)
                }
                else
                {
                    try
                    {
                        when (val remote = poiRepository.getRemotePoi(poiInput.poiId))
                        {
                            null -> LoadResult.Empty
                            else ->
                                LoadResult.Success(
                                    PoiDecision(
                                        data = remote,
                                        showPoiInfo = true
                                    )
                                )
                        }
                    } catch (e: Exception)
                    {
                        LoadResult.Failure(FailureReason.EXCEPTION, e)
                    }
                }
            }

        val weatherResult: LoadResult<WeatherDecision> =
            weatherRepository.getCachedWeather(poiInput.poiId)?.let {
                LoadResult.Success(
                    WeatherDecision(
                        data = it,
                        showWeather = weatherInput.userPrefShowWeather
                    )
                )
            } ?: run {
                if (!weatherInput.networkAvailable)
                {
                    LoadResult.Failure(FailureReason.NO_NETWORK)
                }
                else if (poiResult !is LoadResult.Success)
                {
                    LoadResult.Failure(FailureReason.DEPENDENCY_UNAVAILABLE)
                }
                else
                {
                    try
                    {
                        when (
                            val remote =
                                weatherRepository.getRemoteWeather(
                                    poiResult.data.data.cityName
                                )
                        )
                        {
                            null -> LoadResult.Empty
                            else ->
                                LoadResult.Success(
                                    WeatherDecision(
                                        data = remote,
                                        showWeather = weatherInput.userPrefShowWeather
                                    )
                                )
                        }
                    } catch (e: Exception)
                    {
                        LoadResult.Failure(FailureReason.EXCEPTION, e)
                    }
                }
            }

        return Pair(poiResult, weatherResult)
    }
}
