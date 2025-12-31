package com.example.trevia.data.remote.amap

import com.amap.api.services.core.PoiItem
import com.amap.api.services.weather.LocalWeatherLive
import com.example.trevia.data.local.schedule.PoiWeatherCache
import com.example.trevia.data.local.schedule.PoiWeatherCacheDao
import com.example.trevia.domain.location.model.PoiDetailModel
import com.example.trevia.domain.amap.model.PoiWithWeatherModel
import com.example.trevia.domain.location.model.WeatherModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PoiWeatherRepository @Inject constructor(
    private val aMapService: AMapService,
    private val cacheDao: PoiWeatherCacheDao
) {

    private val cacheTimeoutMs = 30 * 60_000L // 缓存有效期 30 分钟

    suspend fun getPoiWithWeather(poiId: String): PoiWithWeatherModel {
        val now = System.currentTimeMillis()

        // 1. 尝试读取缓存（只有在缓存完整时才返回）
        val cached = cacheDao.getPoiWeather(poiId)
        if (cached != null && now - cached.lastUpdated < cacheTimeoutMs) {
            return PoiWithWeatherModel(
                poi = PoiDetailModel(
                    poiId = cached.poiId,
                    address = cached.poiAddress,
                    tel = cached.poiTel,
                    website = cached.poiWebsite,
                    postCode = cached.poiPostCode,
                    email = cached.poiEmail
                ),
                weather = WeatherModel(
                    weather = cached.weather,
                    temperature = cached.temperature,
                    windDirection = cached.windDirection,
                    windPower = cached.windPower,
                    humidity = cached.humidity,
                    reportTime = cached.reportTime
                )
            )
        }

        // 2. POI 查询（主数据）
        val poiItem = aMapService.getPoiById(poiId)
        if (poiItem == null) {
            // POI 不存在：主数据缺失
            return PoiWithWeatherModel(
                poi = null,
                weather = null
            )
        }

        val poiModel = poiItem.toPoiDetailModel()

        // 3. 天气查询（增强数据，允许为空）
        val city = poiItem.cityName ?: return PoiWithWeatherModel(
            poi = poiModel,
            weather = null
        )

        val weatherLive = aMapService.getLiveWeather(city)

        val weatherModel = weatherLive?.toWeatherModel()

        // 4. 只有在天气存在时才写缓存
        if (weatherModel != null) {
            cacheDao.insertPoiWeather(
                PoiWeatherCache(
                    poiId = poiModel.poiId,
                    poiTel = poiModel.tel,
                    poiAddress = poiModel.address,
                    poiWebsite = poiModel.website,
                    poiPostCode = poiModel.postCode,
                    poiEmail = poiModel.email,
                    weather = weatherModel.weather,
                    temperature = weatherModel.temperature,
                    windDirection = weatherModel.windDirection,
                    windPower = weatherModel.windPower,
                    humidity = weatherModel.humidity,
                    reportTime = weatherModel.reportTime,
                    lastUpdated = now
                )
            )
        }

        // 5. 返回结果（weather 可能为 null）
        return PoiWithWeatherModel(
            poi = poiModel,
            weather = weatherModel
        )
    }

    private fun PoiItem.toPoiDetailModel(): PoiDetailModel = PoiDetailModel(
        poiId = poiId,
        address = snippet,
        tel = tel,
        website = website,
        postCode = postcode,
        email = email
    )

    private fun LocalWeatherLive.toWeatherModel(): WeatherModel = WeatherModel(
        weather = weather ?: "",
        temperature = temperature ?: "",
        windDirection = windDirection ?: "",
        windPower = windPower ?: "",
        humidity = humidity ?: "",
        reportTime = reportTime ?: ""
    )
}
