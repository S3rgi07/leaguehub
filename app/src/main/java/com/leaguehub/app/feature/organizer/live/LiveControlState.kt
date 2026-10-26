package com.leaguehub.app.feature.organizer.live

data class LiveControlState(
    val homeScore: Int,
    val awayScore: Int,
    val matchTime: String,
    val recentEvents: List<LiveEventUiModel>,
    val isPaused: Boolean,
    val isFinished: Boolean = false
)
