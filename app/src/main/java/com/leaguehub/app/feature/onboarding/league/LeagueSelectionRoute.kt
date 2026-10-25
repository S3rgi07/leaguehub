package com.leaguehub.app.feature.onboarding.league

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.leaguehub.app.LeagueHubApplication

@Composable
fun LeagueSelectionRoute(
    onContinue: (String?) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val application = LocalContext.current.applicationContext
            as LeagueHubApplication

    val viewModel: LeagueSelectionViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                LeagueSelectionViewModel(
                    sessionRepository = application.userSessionRepository
                )
            }
        }
    )

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