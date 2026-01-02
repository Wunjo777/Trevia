package com.example.trevia.data.local

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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

    // 用户偏好：是否显示天气
//    fun userPrefShowWeather(): Boolean = userPrefsRepo.getShowWeather()
}