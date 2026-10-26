package com.leaguehub.app.feature.organizer.result

data class PublishedResultState(
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: Int,
    val awayScore: Int,
    val matchday: Int,
    val venue: String,
    val publishedTime: String = "21:18",
    val viewerCount: Int = 1248,
    val updatedPlayers: Int = 6
)
