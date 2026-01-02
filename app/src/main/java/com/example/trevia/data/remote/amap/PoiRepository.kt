package com.example.trevia.data.remote.amap

import com.amap.api.services.core.PoiItem
import com.amap.api.services.weather.LocalWeatherLive
import com.example.trevia.data.local.schedule.PoiCache
import com.example.trevia.data.local.schedule.PoiCacheDao
import com.example.trevia.data.local.schedule.WeatherCache
import com.example.trevia.domain.location.model.PoiDetailModel
import com.example.trevia.domain.location.model.WeatherModel
import com.example.trevia.utils.toUtcMillis
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PoiRepository @Inject constructor(
    private val aMapService: AMapService,
    private val poiCacheDao: PoiCacheDao
)
{
    companion object
    {
        const val POI_CACHE_TIMEOUT_MS = 24 * 60 * 60 * 1_000L // 缓存有效期 1 天
    }

    suspend fun getCachedPoi(poiId: String): PoiDetailModel?
    {
        val now = System.currentTimeMillis()

        val cached = poiCacheDao.getPoiCache(poiId) ?: return null
        if (now - cached.updatedAt > POI_CACHE_TIMEOUT_MS) return null

        return PoiDetailModel(
            poiId = cached.poiId,
            cityName = cached.poiCityName,
            address = cached.poiAddress,
            tel = cached.poiTel,
            website = cached.poiWebsite,
            postCode = cached.poiPostCode,
            email = cached.poiEmail
        )
    }

    suspend fun getRemotePoi(poiId: String): PoiDetailModel?
    {
        val poiItem = aMapService.getPoiById(poiId)
        return poiItem?.toPoiDetailModel()
    }

    suspend fun updatePoiCache(
        poiDetailModel: PoiDetailModel
    )
    {
        poiCacheDao.insertPoiCache(
            PoiCache(
                poiId = poiDetailModel.poiId,
                poiCityName = poiDetailModel.cityName,
                poiTel = poiDetailModel.tel,
                poiWebsite = poiDetailModel.website,
                poiPostCode = poiDetailModel.postCode,
                poiEmail = poiDetailModel.email,
                poiAddress = poiDetailModel.address,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    private fun PoiItem.toPoiDetailModel(): PoiDetailModel = PoiDetailModel(
        poiId = poiId,
        cityName = cityName,
        address = snippet,
        tel = tel,
        website = website,
        postCode = postcode,
        email = email
    )


}
