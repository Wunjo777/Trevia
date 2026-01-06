package com.example.trevia.ui.utils

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.ExoDatabaseProvider
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSink
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

@UnstableApi
object VideoCache {
    private const val MAX_CACHE_SIZE: Long = 200L * 1024L * 1024L // 200 MB
    @Volatile
    private var simpleCache: SimpleCache? = null

    //双重检查锁（Double-Checked Locking）确保在多线程环境下，simpleCache 只被初始化一次。
    private fun initIfNeeded(context: Context) {
        if (simpleCache == null) {
            synchronized(this) {
                if (simpleCache == null) {
                    val cacheDir = File(context.cacheDir, "video_cache")
                    if (!cacheDir.exists()) cacheDir.mkdirs()
                    val dbProvider = ExoDatabaseProvider(context)
                    val evictor = LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE)
                    simpleCache = SimpleCache(cacheDir, evictor, dbProvider)
                }
            }
        }
    }

    /**
     * 返回一个基于 SimpleCache 的 CacheDataSource.Factory。
     * 上游使用 DefaultDataSource.Factory + DefaultHttpDataSource.Factory。
     */
    fun getCacheDataSourceFactory(context: Context): CacheDataSource.Factory {
        initIfNeeded(context)
        val upstreamFactory = DefaultDataSource.Factory(
            context,
            DefaultHttpDataSource.Factory()
        )
        val cacheWriteSinkFactory = CacheDataSink.Factory()
            .setCache(simpleCache!!)
            .setFragmentSize(CacheDataSink.DEFAULT_FRAGMENT_SIZE) // 使用默认片段
        return CacheDataSource.Factory()
            .setCache(simpleCache!!)
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setCacheWriteDataSinkFactory(cacheWriteSinkFactory)
            .setFlags(CacheDataSource.FLAG_BLOCK_ON_CACHE or CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }
}
