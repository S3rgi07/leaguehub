package com.leaguehub.app.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.leaguehub.app.core.components.PlayerDestination
import com.leaguehub.app.feature.matches.model.MatchUiModel

@Composable
fun HomeRoute(
    onNavigate: (PlayerDestination) -> Unit,
    onMatchClick: (MatchUiModel) -> Unit,
    onLeagueClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeScreen(
        state.player, state.league, state.nextMatch, state.recentResults, state.prediction,
        onNavigate, onMatchClick, onLeagueClick, onNotificationsClick
    )
}
