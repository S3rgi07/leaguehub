package com.leaguehub.app.feature.organizer.live

import androidx.compose.runtime.Composable

@Composable
fun LiveControlRoute() {
    LiveControlScreen(
        homeScore = 2,
        awayScore = 1,
        matchTime = "75:24",
        recentEvents = listOf(
            LiveEventUiModel(
                id = "1",
                minute = "68'",
                title = "Gol de S. Méndez",
                description = "Falcons · 2–1"
            ),
            LiveEventUiModel(
                id = "2",
                minute = "61'",
                title = "Entra J. Castillo",
                description = "Titans · Sustitución"
            )
        )
    )
}

