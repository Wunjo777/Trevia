package com.example.trevia.domain.location.decision

import android.util.Log
import com.example.trevia.data.local.cache.CachePolicy.POI_TIMEOUT_MS
import com.example.trevia.data.local.cache.CachePolicy.WEATHER_TIMEOUT_MS
import com.example.trevia.data.remote.amap.PoiRepository
import com.example.trevia.data.remote.amap.WeatherRepository
import com.example.trevia.domain.location.model.FailureReason
import com.example.trevia.domain.location.model.LoadResult
import com.example.trevia.domain.location.model.PoiDecision
import com.example.trevia.domain.location.model.PoiInputs
import com.example.trevia.domain.location.model.WeatherDecision
import com.example.trevia.domain.location.model.WeatherInputs
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
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
                // 更新缓存的最后访问时间
                poiRepository.updatePoiCacheLastAccess(poiInput.poiId)
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
                        // 明确使用 withTimeout，并区分超时与返回 null
                        val remote = withTimeout(POI_TIMEOUT_MS) {
                            poiRepository.getRemotePoi(poiInput.poiId)
                        }
                        when (remote)
                        {
                            null -> LoadResult.Empty
                            else ->
                            {
                                // 缓存远端数据
                                poiRepository.upsertPoiCache(remote)
                                LoadResult.Success(
                                    PoiDecision(
                                        data = remote,
                                        showPoiInfo = true
                                    )
                                )
                            }
                        }
                    } catch (e: TimeoutCancellationException)
                    {
                        // 远端调用超时（映射为领域失败）
                        LoadResult.Failure(FailureReason.TIMEOUT, e)
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
                        val remote =
                            withTimeout(WEATHER_TIMEOUT_MS) {
                                weatherRepository.getRemoteWeather(
                                    poiResult.data.data.cityName
                                )
                            }
                        when (remote)
                        {
                            null -> LoadResult.Empty
                            else ->
                            {
                                // 缓存远端数据
                                weatherRepository.upsertWeatherCache(poiInput.poiId,remote)
                                LoadResult.Success(
                                    WeatherDecision(
                                        data = remote,
                                        showWeather = weatherInput.userPrefShowWeather
                                    )
                                )
                                }
                        }
                    }catch (e: TimeoutCancellationException)
                    {
                        // 远端调用超时（映射为领域失败）
                        LoadResult.Failure(FailureReason.TIMEOUT, e)
                    }
                    catch (e: Exception)
                    {
                        Log.e("EEE", "Weather fetch error: ${e.message}")
                        LoadResult.Failure(FailureReason.EXCEPTION, e)
                    }
                }
            }

        return Pair(poiResult, weatherResult)
    }
}
