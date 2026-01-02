package com.example.trevia.data.remote.amap

import com.amap.api.services.weather.LocalWeatherLive
import com.example.trevia.data.local.schedule.WeatherCache
import com.example.trevia.data.local.schedule.WeatherCacheDao
import com.example.trevia.domain.location.model.WeatherModel
import com.example.trevia.utils.toUtcMillis
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.text.compareTo

@Singleton
class WeatherRepository @Inject constructor(
    private val aMapService: AMapService,
    private val weatherCacheDao: WeatherCacheDao
)
{
    companion object
    {
        const val WEATHER_CACHE_TIMEOUT_MS = 30 * 60_000L // 缓存有效期 30 分钟
    }

    suspend fun getCachedWeather(
        poiId: String
    ): WeatherModel?
    {
        val now = System.currentTimeMillis()

        val cached = weatherCacheDao.getWeatherCache(poiId) ?: return null
        if (now - cached.reportTime > WEATHER_CACHE_TIMEOUT_MS) return null

        return WeatherModel(
            weather = cached.weather,
            temperature = cached.temperature,
            windDirection = cached.windDirection,
            windPower = cached.windPower,
            humidity = cached.humidity,
            reportTime = cached.reportTime
        )
    }

    suspend fun getRemoteWeather(city: String): WeatherModel?
    {
        val weatherLive = aMapService.getLiveWeather(city)
        return weatherLive?.toWeatherModel()
    }

    suspend fun updateWeatherCache(
        poiId: String,
        weatherModel: WeatherModel
    )
    {
        weatherCacheDao.insertWeatherCache(
            WeatherCache(
                poiId = poiId,
                weather = weatherModel.weather,
                temperature = weatherModel.temperature,
                windDirection = weatherModel.windDirection,
                windPower = weatherModel.windPower,
                humidity = weatherModel.humidity,
                reportTime = weatherModel.reportTime
            )
        )

    }

    private fun LocalWeatherLive.toWeatherModel(): WeatherModel = WeatherModel(
        weather = weather ?: "",
        temperature = temperature ?: "",
        windDirection = windDirection ?: "",
        windPower = windPower ?: "",
        humidity = humidity ?: "",
        reportTime = reportTime?.toUtcMillis() ?: 0L
    )
}