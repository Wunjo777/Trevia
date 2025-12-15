package com.example.trevia.domain.amap.usecase

import com.example.trevia.data.remote.amap.TipRepository
import com.example.trevia.domain.amap.model.TipModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetInputTipsUseCase @Inject constructor(
    private val tipRepository: TipRepository
)
{
    suspend operator fun invoke(keyword: String, city: String): List<TipModel>
    {
        return tipRepository.getInputTips(keyword, city)
    }
}