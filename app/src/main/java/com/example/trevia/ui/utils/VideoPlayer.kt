// File: app/src/main/java/com/example/trevia/ui/utils/VideoPlayer.kt
package com.example.trevia.ui.utils

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerWithLifecycle(
    videoUri: Uri,
    autoPlay: Boolean
) {
    val context = LocalContext.current.applicationContext
    val lifecycleOwner = LocalLifecycleOwner.current

    // 1️⃣ 创建并记住 ExoPlayer
    val exoPlayer = remember(videoUri) {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = autoPlay
        }
    }

    // 2️⃣ 使用缓存工厂构建 MediaSource（从磁盘缓存读取 / 未命中则上游下载并写缓存）
    val cacheDataSourceFactory = remember {
        VideoCache.getCacheDataSourceFactory(context)
    }
    val mediaItem = MediaItem.fromUri(videoUri)
    val mediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory)
        .createMediaSource(mediaItem)

    // 设置 mediaSource 并准备
    exoPlayer.setMediaSource(mediaSource)
    exoPlayer.prepare()

    // 3️⃣ 托管 PlayerView
    AndroidView(
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                useController = true
            }
        }
    )

    // 4️⃣ 生命周期控制播放行为
    DisposableEffect(lifecycleOwner, autoPlay) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (autoPlay) {
                        exoPlayer.play()
                    }
                }

                Lifecycle.Event.ON_PAUSE -> {
                    exoPlayer.pause()
                }

                Lifecycle.Event.ON_DESTROY -> {
                    exoPlayer.release()
                }

                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            // 确保 player 释放（如在 Compose 层面已释放可去掉）
            exoPlayer.release()
        }
    }
}
