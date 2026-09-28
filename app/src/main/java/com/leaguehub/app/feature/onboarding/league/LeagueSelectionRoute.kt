package com.leaguehub.app.feature.onboarding.league

import androidx.compose.runtime.Composable

@Composable
fun LeagueSelectionRoute() {
    LeagueSelectionScreen(
        leagues = emptyList(),
        popularLeagues = emptyList(),
        selectedLeagueId = null
    )
}