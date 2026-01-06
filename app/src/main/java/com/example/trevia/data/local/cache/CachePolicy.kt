package com.example.trevia.data.local.cache

object CachePolicy
{
    const val COMMENT_CACHE_TIMEOUT_MS = 2 * 60 * 60 * 1_000L // 缓存有效期 2 小时
    const val POI_CACHE_TIMEOUT_MS = 7 * 24 * 60 * 60 * 1_000L // 缓存有效期 7 天
    const val WEATHER_CACHE_TIMEOUT_MS = 5 * 60 * 1_000L // 缓存有效期 5 分钟
    const val VIDEO_URL_CACHE_TIMEOUT_MS = 7 * 24 * 60 * 60 * 1_000L // 缓存有效期 7 天
    const val IMG_URL_CACHE_TIMEOUT_MS = 24 * 60 * 60 * 1_000L // 缓存有效期 24 小时


    const val MAX_COMMENT_CACHE_SIZE = 100 // 最大评论缓存数量
    const val MAX_POI_CACHE_SIZE = 50 // 最大POI缓存数量
}

