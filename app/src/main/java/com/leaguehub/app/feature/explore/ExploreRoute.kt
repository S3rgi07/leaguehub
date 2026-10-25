package com.leaguehub.app.feature.explore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ExploreRoute(
    onItemClick: (ExploreItemUiModel) -> Unit,
    onBack: () -> Unit,
    viewModel: ExploreViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val items = state.featuredLeagues + state.popularTeams + state.suggestedPlayers
    ExploreScreen(
        searchQuery = state.searchQuery,
        selectedCategory = state.selectedCategory,
        featuredLeagues = state.featuredLeagues,
        popularTeams = state.popularTeams,
        suggestedPlayers = state.suggestedPlayers,
        onSearchChange = { viewModel.onEvent(ExploreEvent.SearchChanged(it)) },
        onCategoryChange = { viewModel.onEvent(ExploreEvent.CategoryChanged(it)) },
        onItemClick = { id -> items.firstOrNull { it.id == id }?.let(onItemClick) },
        onBackClick = onBack
    )
}
