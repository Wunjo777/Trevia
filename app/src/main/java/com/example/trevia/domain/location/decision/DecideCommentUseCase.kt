package com.example.trevia.domain.location.decision

import com.example.trevia.data.remote.leancloud.CommentRepository
import com.example.trevia.data.remote.leancloud.GetLocationDataRepository
import com.example.trevia.domain.location.model.CommentsDecision
import com.example.trevia.domain.location.model.CommentsInput
import com.example.trevia.domain.location.model.FailureReason
import com.example.trevia.domain.location.model.LoadResult
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DecideCommentUseCase @Inject constructor(
    private val getLocationDataRepository: GetLocationDataRepository,
    private val commentRepository: CommentRepository
) {
    companion object {
        private const val DEFAULT_TIMEOUT_MS: Long = 3000
    }
    suspend operator fun invoke(
        input: CommentsInput
    ): LoadResult<CommentsDecision> {

        return commentRepository.getCachedComments(input.poiId)?.let {
            commentRepository.updateCommentsLastAccess(input.poiId)
            LoadResult.Success(
                CommentsDecision(
                    data = it,
                    showComments = true
                )
            )
        } ?: run {
            if (!input.networkAvailable) {
                LoadResult.Failure(FailureReason.NO_NETWORK)
            } else {
                try {
                    val remote = withTimeout(DEFAULT_TIMEOUT_MS) {
                        getLocationDataRepository.getLocationComments(input.poiId)
                    }
                    when  {
                        remote.isEmpty()  -> LoadResult.Empty
                        else ->
                        {
                            commentRepository.upsertCommentsCache(input.poiId, remote)
                            LoadResult.Success(
                                CommentsDecision(
                                    data = remote,
                                    showComments = true
                                )
                            )
                        }
                    }
                } catch (e: TimeoutCancellationException) {
                    LoadResult.Failure(FailureReason.TIMEOUT, e)
                } catch (e: Exception) {
                    LoadResult.Failure(FailureReason.EXCEPTION, e)
                }
            }
        }
    }
}
