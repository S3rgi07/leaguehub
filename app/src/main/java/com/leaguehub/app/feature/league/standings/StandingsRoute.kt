package com.leaguehub.app.feature.league.standings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.leaguehub.app.core.components.PlayerDestination
import com.leaguehub.app.feature.team.model.TeamUiModel

@Composable
fun StandingsRoute(
    onNavigate: (PlayerDestination) -> Unit,
    onTeamClick: (TeamUiModel) -> Unit,
    onLeagueClick: () -> Unit,
    viewModel: StandingsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    StandingsScreen(
        state.league, state.standings, state.selectedTeamId, state.scope, state.updatedLabel,
        { viewModel.onEvent(StandingsEvent.ScopeChanged(it)) }, onTeamClick, onLeagueClick, onNavigate
    )
}
