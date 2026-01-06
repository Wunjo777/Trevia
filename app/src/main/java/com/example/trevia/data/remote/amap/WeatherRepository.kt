package com.example.trevia.data.remote.amap

import com.amap.api.services.weather.LocalWeatherLive
import com.example.trevia.data.local.cache.CachePolicy.WEATHER_CACHE_TIMEOUT_MS
import com.example.trevia.data.local.cache.WeatherCache
import com.example.trevia.data.local.cache.WeatherCacheDao
import com.example.trevia.domain.location.model.WeatherModel
import com.example.trevia.utils.toUtcMillis
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val aMapService: AMapService,
    private val weatherCacheDao: WeatherCacheDao
)
{
    suspend fun getCachedWeather(
        poiId: String
    ): WeatherModel?
    {
        val now = System.currentTimeMillis()

        val cached = weatherCacheDao.getWeatherCache(poiId) ?: return null
        if (now - cached.updatedAt > WEATHER_CACHE_TIMEOUT_MS) return null

        return WeatherModel(
            weather = cached.weather,
            temperature = cached.temperature,
            windDirection = cached.windDirection,
            windPower = cached.windPower,
            humidity = cached.humidity,
            updatedAt = cached.updatedAt
        )
    }

    suspend fun getRemoteWeather(city: String): WeatherModel?
    {
        val weatherLive = aMapService.getLiveWeather(city)
        return weatherLive?.toWeatherModel()
    }

    suspend fun upsertWeatherCache(
        poiId: String,
        weatherModel: WeatherModel
    )
    {
        weatherCacheDao.upsertWeatherCache(
            WeatherCache(
                poiId = poiId,
                weather = weatherModel.weather,
                temperature = weatherModel.temperature,
                windDirection = weatherModel.windDirection,
                windPower = weatherModel.windPower,
                humidity = weatherModel.humidity,
                updatedAt = weatherModel.updatedAt
            )
        )
    }

    private fun LocalWeatherLive.toWeatherModel(): WeatherModel
    {
        val WeatherTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return WeatherModel(
            weather = weather ?: "",
            temperature = temperature ?: "",
            windDirection = windDirection ?: "",
            windPower = windPower ?: "",
            humidity = humidity ?: "",
            updatedAt = reportTime?.let {
                LocalDateTime.parse(it, WeatherTimeFormatter)
                    .atZone(ZoneId.of("Asia/Shanghai"))
                    .toInstant()
                    .toEpochMilli()
            } ?: 0L
        )
    }
}