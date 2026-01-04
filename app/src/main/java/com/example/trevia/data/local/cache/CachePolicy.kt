package com.example.trevia.data.local.cache

object CachePolicy
{
   const val COMMENT_TIMEOUT_MS = 1  * 1_000L//2 * 60 * 60 * 1_000L // 缓存有效期 2 小时
   const val POI_TIMEOUT_MS = 1 * 1_000L//7 * 24 * 60 * 60 * 1_000L // 缓存有效期 7 天
   const val WEATHER_TIMEOUT_MS = 1 * 1_000L//5 * 60 * 1_000L // 缓存有效期 5 分钟

    const val MAX_COMMENT_CACHE_SIZE = 100 // 最大评论缓存数量
    const val MAX_POI_CACHE_SIZE = 50 // 最大POI缓存数量
}

