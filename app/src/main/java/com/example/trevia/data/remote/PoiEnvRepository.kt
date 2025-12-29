package com.example.trevia.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.PowerManager
import com.example.trevia.data.remote.amap.PoiWeatherRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PoiEnvRepository @Inject constructor(
    private val context: Context,
//    private val userPrefsRepo: UserPrefsRepository // 用于读取用户偏好
)
{

    // 获取网络状态
    fun isNetworkAvailable(): Boolean
    {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val caps = connectivityManager.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // 页面是否可见（示例：Fragment 可见性）
    fun isVisible(): Boolean = true

    // 用户偏好：是否显示天气
//    fun userPrefShowWeather(): Boolean = userPrefsRepo.getShowWeather()
}

