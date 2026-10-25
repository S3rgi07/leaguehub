package com.leaguehub.app.feature.explore

import androidx.compose.runtime.Composable

@Composable
fun ExploreRoute() {
    ExploreScreen(
        searchQuery = "",
        selectedCategory = ExploreCategory.ALL,
        featuredLeagues = emptyList(),
        popularTeams = emptyList(),
        suggestedPlayers = emptyList()
    )
}
