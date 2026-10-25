package com.leaguehub.app.feature.league.detail

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.leaguehub.app.core.components.PlayerDestination
import com.leaguehub.app.feature.team.model.TeamUiModel

@Composable
fun LeagueDetailRoute(
    leagueId: String,
    onTeamClick: (TeamUiModel) -> Unit,
    onBack: () -> Unit,
    onNotificationsClick: () -> Unit,
    onNavigate: (PlayerDestination) -> Unit
) {
    val viewModel: LeagueDetailViewModel = viewModel(
        key = "league-$leagueId",
        factory = viewModelFactory { initializer { LeagueDetailViewModel(leagueId) } }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    LeagueDetailScreen(
        state.league, state.teams, state.news, state.selectedTab,
        { viewModel.onEvent(LeagueDetailEvent.TabChanged(it)) }, onTeamClick,
        { viewModel.onEvent(LeagueDetailEvent.NewsSelected(it.id)) }, onBack, onNotificationsClick, onNavigate
    )
    state.selectedNews?.let { news ->
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(LeagueDetailEvent.NewsDismissed) },
            title = { Text(news.title) },
            text = { Text(news.body) },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(LeagueDetailEvent.NewsDismissed) }) { Text("Entendido") }
            }
        )
    }
}
