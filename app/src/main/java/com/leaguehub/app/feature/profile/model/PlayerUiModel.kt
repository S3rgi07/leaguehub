package com.leaguehub.app.feature.profile.model

data class PlayerUiModel(
    val id: String,
    val name: String,
    val number: Int,
    val position: String,
    val teamId: String,
    val goals: Int,
    val assists: Int,
    val matchesPlayed: Int,
    val yellowCards: Int = 0,
    val redCards: Int = 0,
    val minutesPlayed: Int = 0,
    val recentRatings: List<Double> = emptyList(),
    val rating: Double? = null,
    val isStarter: Boolean = false,
    val isCaptain: Boolean = false,
    val scoringStreak: Int = 0,
    val mvpAwards: Int = 0,
    val goalRank: Int? = null
)
