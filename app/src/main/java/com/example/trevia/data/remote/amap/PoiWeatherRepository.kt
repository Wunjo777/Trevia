package com.example.trevia.data.remote.amap

import com.amap.api.services.core.PoiItem
import com.amap.api.services.weather.LocalWeatherLive
import com.example.trevia.data.local.schedule.PoiWeatherCache
import com.example.trevia.data.local.schedule.PoiWeatherCacheDao
import com.example.trevia.domain.amap.model.PoiDetailModel
import com.example.trevia.domain.amap.model.PoiWithWeatherModel
import com.example.trevia.domain.amap.model.WeatherModel
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

        // 1. 尝试读取缓存
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

        // 2. 缓存不存在或过期 → 从 API 获取
        val poiItem = aMapService.getPoiById(poiId)
        val cityCode = poiItem.adCode ?: throw Exception("POI 缺少城市编码信息")
        val weatherLive = aMapService.getLiveWeather(cityCode)

        val poiModel = poiItem.toPoiDetailModel()
        val weatherModel = weatherLive.toWeatherModel()

        // 3. 更新缓存
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
