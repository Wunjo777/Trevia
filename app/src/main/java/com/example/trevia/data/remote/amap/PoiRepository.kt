package com.example.trevia.data.remote.amap

import com.amap.api.services.core.PoiItem
import com.example.trevia.data.local.cache.CachePolicy.POI_CACHE_TIMEOUT_MS
import com.example.trevia.data.local.cache.PoiCache
import com.example.trevia.data.local.cache.PoiCacheDao
import com.example.trevia.domain.location.model.PoiDetailModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PoiRepository @Inject constructor(
    private val aMapService: AMapService,
    private val poiCacheDao: PoiCacheDao
)
{
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

    suspend fun upsertPoiCache(
        poiDetailModel: PoiDetailModel
    )
    {
        val now = System.currentTimeMillis()
        poiCacheDao.upsertPoiCache(
            PoiCache(
                poiId = poiDetailModel.poiId,
                poiCityName = poiDetailModel.cityName,
                poiTel = poiDetailModel.tel,
                poiWebsite = poiDetailModel.website,
                poiPostCode = poiDetailModel.postCode,
                poiEmail = poiDetailModel.email,
                poiAddress = poiDetailModel.address,
                updatedAt = now,
                lastAccess = now
            )
        )
    }

    suspend fun updatePoiCacheLastAccess(poiId: String)
    {
        val now = System.currentTimeMillis()
        poiCacheDao.updatePoiCacheLastAccess(poiId, now)
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
