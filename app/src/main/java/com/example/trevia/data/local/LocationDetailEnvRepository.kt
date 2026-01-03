package com.example.trevia.data.local

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.PowerManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationDetailEnvRepository @Inject constructor(
    @ApplicationContext private val context: Context,
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

    fun estimateBandwidthKbps(): Int {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return 0
        val caps = connectivityManager.getNetworkCapabilities(network) ?: return 0
        return when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)     -> 10000
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> 2000
            else -> 500
        }
    }

    fun isBatterySaverOn(): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isPowerSaveMode
    }

    // 用户偏好：是否显示天气
//    fun userPrefShowWeather(): Boolean = userPrefsRepo.getShowWeather()

    //    fun userPrefAutoPlay(): Boolean = prefs.getAutoPlay()
}