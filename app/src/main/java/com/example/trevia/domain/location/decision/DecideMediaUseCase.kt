package com.example.trevia.domain.location.decision

import com.example.trevia.domain.location.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DecideMediaUseCase @Inject constructor()
{

    suspend operator fun invoke(input: MediaInputs): MediaDecision
    {

        val media = input.mediaData
            ?: return MediaDecision(
                media = null,
                showMedia = false,
                degradeReason = DegradeReason.UNAVAILABLE
            )

        val hasImage = media.imgUrls.isNotEmpty()
        val hasVideo =
            media.videoUrlSmall != null ||
                    media.videoUrlMedium != null ||
                    media.videoUrlLarge != null

        // 页面不可见：不展示、不加载
        if (!input.isVisible)
        {
            return MediaDecision(
                media = media,
                showMedia = false,
                degradeReason = DegradeReason.NOT_VISIBLE
            )
        }

        // 无网络：只能展示图片
        if (!input.networkAvailable)
        {
            return MediaDecision(
                media = if (hasImage) media else null,
                showMedia = hasImage,
                autoPlayVideo = false,
                videoQuality = null,
                degradeReason = DegradeReason.NO_NETWORK
            )
        }

        // 有网 + 省电模式：禁止视频自动播放
        if (input.isBatterySaverOn && hasVideo)
        {
            return MediaDecision(
                media = media,
                showMedia = true,
                autoPlayVideo = false,
                videoQuality = null,
                degradeReason = DegradeReason.BATTERY_SAVER
            )
        }

        // 正常情况：优先视频，其次图片
        if (hasVideo)
        {
            val quality = when
            {
                input.bandwidthKbps < 500  -> VideoQuality.SMALL
                input.bandwidthKbps < 2000 -> VideoQuality.MEDIUM
                else                       -> VideoQuality.LARGE
            }

            return MediaDecision(
                media = media,
                showMedia = true,
                autoPlayVideo = true,
                videoQuality = quality
            )
        }

        if (hasImage)
        {
            return MediaDecision(
                media = media,
                showMedia = true
            )
        }

        // 理论兜底：既无视频也无图片
        return MediaDecision(
            media = null,
            showMedia = false,
            degradeReason = DegradeReason.UNAVAILABLE
        )
    }
}

