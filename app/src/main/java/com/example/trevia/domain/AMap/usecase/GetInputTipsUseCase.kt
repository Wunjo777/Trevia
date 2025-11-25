package com.example.trevia.domain.AMap.usecase

import com.example.trevia.data.amap.TipRepository
import com.example.trevia.domain.AMap.model.TipModel
import javax.inject.Inject

class GetInputTipsUseCase @Inject constructor(
    private val tipRepository: TipRepository
)
{
    suspend operator fun invoke(keyword: String, city: String): List<TipModel>
    {
        return tipRepository.getInputTips(keyword, city)
    }
}