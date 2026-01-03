package com.example.trevia.domain.location.decision

import com.example.trevia.data.remote.LocationMediaRepository
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
) {

    companion object {
        private const val DEFAULT_TIMEOUT_MS: Long = 3000
    }

    suspend operator fun invoke(
        input: MediaInputs
    ): LoadResult<MediaDecision> {

        if (!input.networkAvailable) {
            return LoadResult.Failure(FailureReason.NO_NETWORK)
        }

        return try {
            val decision = withTimeout(DEFAULT_TIMEOUT_MS) {

                val videoUrls =
                    locationMediaRepository.getFirstVideoUrl(keyword = input.location)

                val userImgUrls =
                    getLocationDataRepository.getLocationImgUrls(input.poiId)

                val webImgUrls =
                    locationMediaRepository.getFirstNImageUrls(
                        keyword = input.location,
                        count = 3
                    )

                if (
                    videoUrls == null &&
                    userImgUrls.isEmpty() &&
                    webImgUrls.isEmpty()
                ) {
                    return@withTimeout null
                }

                val showVideo = videoUrls != null
                val showImage = userImgUrls.isNotEmpty() || webImgUrls.isNotEmpty()

                val targetVideoQuality =
                    if (showVideo) {
                        when (input.bandwidthKbps) {
                            in 0 until 1000    -> VideoQuality.SMALL
                            in 1000 until 3000 -> VideoQuality.MEDIUM
                            else               -> VideoQuality.LARGE
                        }
                    } else {
                        null
                    }

                val finalVideoQuality = targetVideoQuality?.let { quality ->
                    when (quality) {
                        VideoQuality.LARGE ->
                            when {
                                videoUrls?.large != null  -> VideoQuality.LARGE
                                videoUrls?.medium != null -> VideoQuality.MEDIUM
                                videoUrls?.small != null  -> VideoQuality.SMALL
                                else -> null
                            }

                        VideoQuality.MEDIUM ->
                            when {
                                videoUrls?.medium != null -> VideoQuality.MEDIUM
                                videoUrls?.small != null  -> VideoQuality.SMALL
                                videoUrls?.large != null  -> VideoQuality.LARGE
                                else -> null
                            }

                        VideoQuality.SMALL ->
                            when {
                                videoUrls?.small != null  -> VideoQuality.SMALL
                                videoUrls?.medium != null -> VideoQuality.MEDIUM
                                videoUrls?.large != null  -> VideoQuality.LARGE
                                else -> null
                            }
                    }
                }

                val autoPlayVideo =
                    showVideo &&
                            input.userPrefAutoPlayVideo &&
                            !input.isBatterySaverOn

                MediaDecision(
                    data = MediaModel(
                        videoUrlSmall = videoUrls?.small,
                        videoUrlMedium = videoUrls?.medium,
                        videoUrlLarge = videoUrls?.large,
                        imgUrls = webImgUrls + userImgUrls
                    ),
                    showVideo = showVideo,
                    showImage = showImage,
                    autoPlayVideo = autoPlayVideo,
                    videoQuality = finalVideoQuality
                )
            }

            decision?.let { LoadResult.Success(it) } ?: LoadResult.Empty

        } catch (e: TimeoutCancellationException) {
            LoadResult.Failure(FailureReason.TIMEOUT, e)
        } catch (e: Exception) {
            LoadResult.Failure(FailureReason.EXCEPTION, e)
        }
    }
}
