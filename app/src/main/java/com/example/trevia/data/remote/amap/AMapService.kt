package com.example.trevia.data.remote.amap

import android.content.Context
import android.util.Log
import com.amap.api.services.core.PoiItem
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.help.Tip
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.amap.api.services.weather.LocalWeatherForecastResult
import com.amap.api.services.weather.LocalWeatherLive
import com.amap.api.services.weather.LocalWeatherLiveResult
import com.amap.api.services.weather.WeatherSearch
import com.amap.api.services.weather.WeatherSearchQuery
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class AMapService(private val context: Context)
{

    /**
     * 异步获取输入提示
     * @param keyword 用户输入的关键字
     * @param city 限定城市，为空或 null 表示全国
     */
    suspend fun getInputTips(keyword: String, city: String?): List<Tip> =
        suspendCancellableCoroutine { cont ->

            val query = InputtipsQuery(keyword, city)
            val inputTips = Inputtips(context, query)

            cont.invokeOnCancellation {
                inputTips.setInputtipsListener(null) // 防止回调泄露
            }

            inputTips.setInputtipsListener { tipList, rCode ->
                if (cont.isActive)
                {// 检查协程是否已取消，防止回调泄漏
                    if (rCode == 1000) cont.resume(tipList)
                    else cont.resumeWithException(Exception("高德错误码: $rCode"))
                }
            }

            inputTips.requestInputtipsAsyn()
        }

    suspend fun getPoiById(poiId: String): PoiItem? =
        suspendCancellableCoroutine { cont ->

            val poiSearch = PoiSearch(context, null)

            cont.invokeOnCancellation {
                poiSearch.setOnPoiSearchListener(null)
            }

            poiSearch.setOnPoiSearchListener(object : PoiSearch.OnPoiSearchListener
            {

                override fun onPoiItemSearched(item: PoiItem?, rCode: Int)
                {
                    if (!cont.isActive) return

                    when (rCode)
                    {
                        1000 ->
                        {
                            // 成功，但 item 可能为 null
                            // null 表示：POI 不存在 / 已下架 / 无法返回
                            cont.resume(item)
                        }

                        else ->
                        {
                            cont.resumeWithException(
                                IllegalStateException(
                                    "高德 POI ID 检索失败，错误码: $rCode"
                                )
                            )
                        }
                    }
                }

                override fun onPoiSearched(result: PoiResult?, rCode: Int)
                {
                    // ID 查询不使用该回调
                }
            })

            poiSearch.searchPOIIdAsyn(poiId)
        }


    suspend fun getLiveWeather(city: String): LocalWeatherLive? =
        suspendCancellableCoroutine { cont ->

            val query = WeatherSearchQuery(
                city,
                WeatherSearchQuery.WEATHER_TYPE_LIVE
            )

            val weatherSearch = WeatherSearch(context)
            weatherSearch.query = query

            cont.invokeOnCancellation {
                weatherSearch.setOnWeatherSearchListener(null)
            }

            weatherSearch.setOnWeatherSearchListener(
                object : WeatherSearch.OnWeatherSearchListener
                {

                    override fun onWeatherLiveSearched(
                        result: LocalWeatherLiveResult?,
                        rCode: Int
                    )
                    {

                        if (!cont.isActive)
                        {
                            return
                        }

                        if (rCode == 1000 && result?.liveResult != null)
                        {
                            val live = result.liveResult

                            cont.resume(live)
                        }
                        else
                        {

                            cont.resumeWithException(
                                Exception(
                                    "高德天气查询失败，错误码: $rCode, city=$city"
                                )
                            )
                        }
                    }

                    override fun onWeatherForecastSearched(
                        result: LocalWeatherForecastResult?,
                        rCode: Int
                    )
                    {
                        //实时天气不使用该回调
                    }
                }
            )

            weatherSearch.searchWeatherAsyn()
        }

}