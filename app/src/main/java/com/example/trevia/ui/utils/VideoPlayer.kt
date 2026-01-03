package com.example.trevia.ui.utils

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun VideoPlayerWithLifecycle(
    videoUri: Uri,
    autoPlay: Boolean
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 1️⃣ 创建并记住 ExoPlayer
    val exoPlayer = remember(videoUri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
            playWhenReady = autoPlay
        }
    }

    // 2️⃣ 托管 PlayerView
    AndroidView(
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                useController = true
            }
        }
    )

    // 3️⃣ 生命周期控制播放行为
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
        }
    }
}
