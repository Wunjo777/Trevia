package com.example.trevia.domain.amap.model

import com.example.trevia.ui.schedule.TripDetail.LocationTipUiState

data class TipModel(
    val poiId: String,
    val name: String,
    val district: String,
    val address: String,
    val latitude: Double?,
    val longitude: Double?
)

fun TipModel.toLocationTipUiState() = LocationTipUiState(poiId, name, address, latitude, longitude)
