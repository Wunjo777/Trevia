package com.example.trevia.domain.location.decision

import android.util.Log
import com.example.trevia.data.local.cache.CachePolicy.IMG_URL_CACHE_TIMEOUT_MS
import com.example.trevia.data.local.cache.CachePolicy.VIDEO_URL_CACHE_TIMEOUT_MS
import com.example.trevia.data.remote.LocationMediaRepository
import com.example.trevia.data.remote.VideoUrlResult
import com.example.trevia.data.remote.VideoUrls
import com.example.trevia.data.remote.leancloud.GetLocationDataRepository
import com.example.trevia.domain.location.model.*
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DecideMediaUseCase @Inject constructor(
    private val locationMediaRepository: LocationMediaRepository,
    private val getLocationDataRepository: GetLocationDataRepository
)
{

    companion object
    {
        private const val DEFAULT_TIMEOUT_MS: Long = 5000
    }

    suspend operator fun invoke(
        input: MediaInputs
    ): LoadResult<MediaDecision>
    {

        val now = System.currentTimeMillis()

        // ---------- 1️⃣ Cache ----------
        val cachedVideo =
            locationMediaRepository.getVideoUrlByPoi(input.poiId)
                ?.takeIf { now - it.updatedAt < VIDEO_URL_CACHE_TIMEOUT_MS }
                ?.let {
                    VideoUrlResult(
                        it.videoUrlSmall,
                        it.videoUrlMedium,
                        it.videoUrlLarge
                    )
                }

        val cachedImages =
            locationMediaRepository.getImgUrlsByPoi(input.poiId)
                .filter { now - it.updatedAt < IMG_URL_CACHE_TIMEOUT_MS }
                .map { it.imgUrl }

        Log.d("DecideMediaUseCase", "cachedVideo: $cachedVideo")
        Log.d("DecideMediaUseCase", "cachedImages: $cachedImages")

        // ---------- 2️⃣ Network supplement ----------
        if (!input.networkAvailable && cachedVideo == null && cachedImages.isEmpty())
        {
            return LoadResult.Failure(FailureReason.NO_NETWORK)
        }

        return try
        {
            val (videoUrls, imgUrls) = withTimeout(DEFAULT_TIMEOUT_MS) {

                val finalVideo =
                    cachedVideo ?: run {
                        if (input.networkAvailable)
                        {
                            val video = locationMediaRepository.getFirstVideoUrl(
                                keyword = input.location
                            )
                            video?.let {
                                locationMediaRepository.upsertVideoUrl(
                                    poiId = input.poiId,
                                    video = VideoUrlResult(
                                        small = it.small,
                                        medium = it.medium,
                                        large = it.large
                                    )
                                )
                            }
                            video
                        }
                        else null
                    }

                val finalImages =
                    if (cachedImages.isNotEmpty())
                    {
                        cachedImages
                    }
                    else if (input.networkAvailable)
                    {
                        val userImgs =
                            getLocationDataRepository.getLocationImgUrls(input.poiId)
                        val webImgs =
                            locationMediaRepository.getFirstNImageUrls(
                                keyword = input.location,
                                count = 3
                            )
                        val imgs = userImgs + webImgs
                        if (imgs.isNotEmpty())
                        {
                            locationMediaRepository.upsertImgUrls(
                                poiId = input.poiId,
                                imgUrls = imgs
                            )
                        }

                        imgs
                    }
                    else emptyList()

                finalVideo to finalImages
            }

            when
            {
                videoUrls == null && imgUrls.isEmpty() ->
                    LoadResult.Empty

                else                                   ->
                    LoadResult.Success(
                        buildMediaDecision(
                            input = input,
                            videoUrls = videoUrls,
                            imgUrls = imgUrls
                        )
                    )
            }

        } catch (e: TimeoutCancellationException)
        {
            LoadResult.Failure(FailureReason.TIMEOUT, e)
        } catch (e: Exception)
        {
            LoadResult.Failure(FailureReason.EXCEPTION, e)
        }
    }

    private fun buildMediaDecision(
        input: MediaInputs,
        videoUrls: VideoUrlResult?,
        imgUrls: List<String>
    ): MediaDecision
    {

        val showVideo = videoUrls != null
        val showImage = imgUrls.isNotEmpty()

        val targetVideoQuality =
            if (showVideo)
            {
                when (input.bandwidthKbps)
                {
                    in 0 until 1000    -> VideoQuality.SMALL
                    in 1000 until 3000 -> VideoQuality.MEDIUM
                    else               -> VideoQuality.LARGE
                }
            }
            else null

        val finalVideoQuality = targetVideoQuality?.let { quality ->
            when (quality)
            {
                VideoQuality.LARGE  ->
                    when
                    {
                        videoUrls?.large != null  -> VideoQuality.LARGE
                        videoUrls?.medium != null -> VideoQuality.MEDIUM
                        videoUrls?.small != null  -> VideoQuality.SMALL
                        else                      -> null
                    }

                VideoQuality.MEDIUM ->
                    when
                    {
                        videoUrls?.medium != null -> VideoQuality.MEDIUM
                        videoUrls?.small != null  -> VideoQuality.SMALL
                        videoUrls?.large != null  -> VideoQuality.LARGE
                        else                      -> null
                    }

                VideoQuality.SMALL  ->
                    when
                    {
                        videoUrls?.small != null  -> VideoQuality.SMALL
                        videoUrls?.medium != null -> VideoQuality.MEDIUM
                        videoUrls?.large != null  -> VideoQuality.LARGE
                        else                      -> null
                    }
            }
        }

        val autoPlayVideo =
            showVideo &&
                    input.userPrefAutoPlayVideo &&
                    !input.isBatterySaverOn

        return MediaDecision(
            data = MediaModel(
                videoUrlSmall = videoUrls?.small,
                videoUrlMedium = videoUrls?.medium,
                videoUrlLarge = videoUrls?.large,
                imgUrls = imgUrls
            ),
            showVideo = showVideo,
            showImage = showImage,
            autoPlayVideo = autoPlayVideo,
            videoQuality = finalVideoQuality
        )
    }
}
