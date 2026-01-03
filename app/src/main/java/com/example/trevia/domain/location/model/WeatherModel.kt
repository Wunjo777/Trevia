package com.example.trevia.domain.location.model

import com.example.trevia.data.local.cache.WeatherCache

data class WeatherModel(
    val weather: String,        // 天气情况
    val temperature: String,    // 温度
    val windDirection: String,  // 风向
    val windPower: String,      // 风力
    val humidity: String,       // 湿度
    val updatedAt: Long      // 发布时间
)

fun WeatherModel.toWeatherCache(poiId:String,weatherModel: WeatherModel)
{
    WeatherCache(
        poiId = poiId,
        weather = weatherModel.weather,
        temperature = weatherModel.temperature,
        windDirection = weatherModel.windDirection,
        windPower = weatherModel.windPower,
        humidity = weatherModel.humidity,
        updatedAt = weatherModel.updatedAt
    )
}