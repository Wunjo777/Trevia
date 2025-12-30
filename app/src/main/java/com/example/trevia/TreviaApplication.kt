package com.example.trevia

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import cn.leancloud.LeanCloud
import com.amap.api.location.AMapLocationClient
import dagger.hilt.android.HiltAndroidApp
import jakarta.inject.Inject

@HiltAndroidApp
class TreviaApplication : Application(), Configuration.Provider
{
    override fun onCreate()
    {
        super.onCreate()

        LeanCloud.initialize(this, "Cx0W1RPiUW0McHA4lrHoEkg9-gzGzoHsz", "jwXZesjK368srVA0E3oWmYnh", "https://cx0w1rpi.lc-cn-n1-shared.com")

        // 高德隐私合规
        AMapLocationClient.updatePrivacyShow(this, true, true)
        AMapLocationClient.updatePrivacyAgree(this, true)
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}