package com.leaguehub.app.feature.organizer.validation.events

data class EventValidationState(
    val events: List<DetectedEventUiModel>,
    val homeScore: Int,
    val awayScore: Int,
    val lineupCount: Int = 22,
    val isPublished: Boolean = false
)
