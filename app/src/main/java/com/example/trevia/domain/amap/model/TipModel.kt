package com.example.trevia.domain.amap.model

import com.example.trevia.ui.schedule.LocationTipUiState

data class TipModel(
    val poiId: String,
    val name: String,
    val district: String,
    val address: String
)

fun TipModel.toLocationTipUiState() = LocationTipUiState(poiId, name, address)
