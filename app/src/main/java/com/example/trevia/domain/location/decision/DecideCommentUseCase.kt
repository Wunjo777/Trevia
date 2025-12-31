package com.example.trevia.domain.location.decision

import com.example.trevia.domain.location.model.CommentDecision
import com.example.trevia.domain.location.model.CommentInputs
import com.example.trevia.domain.location.model.DegradeReason
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DecideCommentUseCase @Inject constructor()
{
    operator fun invoke(input: CommentInputs): CommentDecision
    {
        // 网络不可用 → 降级
        if (!input.networkAvailable)
        {
            return CommentDecision(
                comments = null,
                showComments = false,
                degradeReason = DegradeReason.NO_NETWORK
            )
        }

        // 没有评论：这是正常情况！
        if (input.comments == null)
        {
            return CommentDecision(
                comments = emptyList(),
                showComments = true,
                degradeReason = null
            )
        }

        // 正常展示
        return CommentDecision(
            comments = input.comments,
            showComments = true,
            degradeReason = null
        )
    }
}