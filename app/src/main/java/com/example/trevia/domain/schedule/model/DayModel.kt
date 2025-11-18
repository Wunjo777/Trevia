package com.example.trevia.domain.schedule.model

data class DayModel(
    val date: String,
    val activities: List<EventModel>
)