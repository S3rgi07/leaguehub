package com.leaguehub.app.feature.onboarding.league

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LeagueSelectionRoute(
    onContinue: (String?) -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: LeagueSelectionViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(
        state.isSelectionConfirmed,
        state.isSkipped
    ) {
        if (state.isSelectionConfirmed || state.isSkipped) {
            onContinue(state.selectedLeagueId)
        }
    }

    LeagueSelectionScreen(
        leagues = state.leagues,
        popularLeagues = state.popularLeagues,
        selectedLeagueId = state.selectedLeagueId,
        searchQuery = state.searchQuery,

        onSearchChange = { query ->
            viewModel.onEvent(
                LeagueSelectionEvent.SearchChanged(query)
            )
        },

        onLeagueSelected = { leagueId ->
            viewModel.onEvent(
                LeagueSelectionEvent.LeagueSelected(leagueId)
            )
        },

        onContinueClick = {
            viewModel.onEvent(
                LeagueSelectionEvent.ContinueClicked
            )
        },

        onExploreLaterClick = {
            viewModel.onEvent(
                LeagueSelectionEvent.ExploreLaterClicked
            )
        },

        onBackClick = onBack
    )
}