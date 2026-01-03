package com.example.trevia

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import cn.leancloud.LeanCloud
import com.amap.api.location.AMapLocationClient
import com.example.trevia.data.local.cache.CachePolicy.COMMENT_TIMEOUT_MS
import com.example.trevia.data.local.cache.CachePolicy.MAX_COMMENT_CACHE_SIZE
import com.example.trevia.data.local.cache.CachePolicy.MAX_POI_CACHE_SIZE
import com.example.trevia.data.local.cache.CachePolicy.POI_TIMEOUT_MS
import com.example.trevia.data.local.cache.CachePolicy.WEATHER_TIMEOUT_MS
import com.example.trevia.utils.CacheCleaner
import com.example.trevia.work.TaskScheduler
import dagger.Lazy
import dagger.hilt.android.HiltAndroidApp
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltAndroidApp
class TreviaApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var cacheCleaner: CacheCleaner
    @Inject
    lateinit var appScope: AppCoroutineScope
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    @Inject
    lateinit var taskScheduler: TaskScheduler


    override fun onCreate() {
        super.onCreate()

        // LeanCloud 初始化
        LeanCloud.initialize(
            this,
            "Cx0W1RPiUW0McHA4lrHoEkg9-gzGzoHsz",
            "jwXZesjK368srVA0E3oWmYnh",
            "https://cx0w1rpi.lc-cn-n1-shared.com"
        )

        // 高德隐私合规
        AMapLocationClient.updatePrivacyShow(this, true, true)
        AMapLocationClient.updatePrivacyAgree(this, true)

        // 启动同步任务
        appScope.scope.launch {
            runCatching {
                taskScheduler.scheduleSync()
            }.onFailure {
                Log.e("TreviaApp", "Sync failed", it)
            }
        }

        // 启动缓存清理
        appScope.scope.launch {
            runCatching {
                cacheCleaner.clean(
                    poiExpireDurationMs = POI_TIMEOUT_MS,
                    commentExpireDurationMs = COMMENT_TIMEOUT_MS,
                    weatherExpireDurationMs = WEATHER_TIMEOUT_MS,
                    maxCommentSize = MAX_COMMENT_CACHE_SIZE,
                    maxPoiSize = MAX_POI_CACHE_SIZE
                )
            }.onFailure {
                Log.e("TreviaApp", "Cache cleaning failed", it)
            }
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}
