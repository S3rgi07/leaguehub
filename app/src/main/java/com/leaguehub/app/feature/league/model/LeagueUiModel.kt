package com.leaguehub.app.feature.league.model

data class LeagueUiModel(
    val id: String,
    val name: String,
    val season: String,
    val currentMatchday: Int,
    val totalMatchdays: Int,
    val teamCount: Int,
    val playerCount: Int,
    val matchCount: Int = 0,
    val isActive: Boolean = true
)
