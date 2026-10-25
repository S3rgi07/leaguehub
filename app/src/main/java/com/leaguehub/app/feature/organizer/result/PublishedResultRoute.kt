package com.leaguehub.app.feature.organizer.result

import androidx.compose.runtime.Composable

@Composable
fun PublishedResultRoute() {
    PublishedResultScreen(
        homeTeam = "Falcons",
        awayTeam = "Titans",
        homeScore = 2,
        awayScore = 1,
        matchday = 8,
        venue = "Cancha Central",
        publishedTime = "21:18",
        viewerCount = 1248,
        updatedPlayers = 6
    )
}
