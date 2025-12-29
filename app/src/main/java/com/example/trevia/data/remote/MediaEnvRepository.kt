package com.example.trevia.data.remote

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.PowerManager

@Singleton
class MediaEnvRepository @Inject constructor(
    @ApplicationContext private val context: Context,
//    private val prefs: UserPrefsRepository // 用户偏好 Repository
) {

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

    fun isVisible(): Boolean {
        // 简单演示，实际可由 Fragment / RecyclerView 回调
        return true
    }

//    fun userPrefAutoPlay(): Boolean = prefs.getAutoPlay()

    fun isBatterySaverOn(): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isPowerSaveMode
    }
}
