package com.leaguehub.app.feature.matches.matchcenter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.leaguehub.app.LeagueHubApplication

@Composable
fun MatchCenterRoute(
    matchId: String,
    onBack: () -> Unit,
    onShare: () -> Unit
) {
    val application = LocalContext.current.applicationContext as LeagueHubApplication
    val viewModel: MatchCenterViewModel = viewModel(
        key = "match-center-$matchId",
        factory = viewModelFactory {
            initializer { MatchCenterViewModel(matchId, application.matchWorkflowRepository) }
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    MatchCenterScreen(
        match = state.match,
        leagueName = state.leagueName,
        selectedTab = state.selectedTab,
        events = state.events,
        lineups = state.lineups,
        selectedTeamId = state.selectedTeamId,
        statistics = state.statistics,
        momentum = state.momentum,
        onTabSelected = { viewModel.onEvent(MatchCenterEvent.TabSelected(it)) },
        onTeamSelected = { viewModel.onEvent(MatchCenterEvent.TeamSelected(it)) },
        onBack = onBack,
        onShare = onShare
    )
}
