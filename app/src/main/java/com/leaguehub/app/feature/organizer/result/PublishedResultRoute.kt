package com.leaguehub.app.feature.organizer.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.leaguehub.app.LeagueHubApplication

@Composable
fun PublishedResultRoute(
    matchId: String,
    onMatchCenter: (String) -> Unit,
    onDashboard: () -> Unit
) {
    val application = LocalContext.current.applicationContext as LeagueHubApplication
    val viewModel: PublishedResultViewModel = viewModel(
        key = "published-$matchId",
        factory = viewModelFactory {
            initializer { PublishedResultViewModel(application.matchWorkflowRepository) }
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    PublishedResultScreen(
        homeTeam = state.homeTeam,
        awayTeam = state.awayTeam,
        homeScore = state.homeScore,
        awayScore = state.awayScore,
        matchday = state.matchday,
        venue = state.venue,
        publishedTime = state.publishedTime,
        viewerCount = state.viewerCount,
        updatedPlayers = state.updatedPlayers,
        onMatchCenterClick = {
            viewModel.onEvent(PublishedResultEvent.MatchCenterClicked)
            onMatchCenter(matchId)
        },
        onBackToDashboardClick = {
            viewModel.onEvent(PublishedResultEvent.DashboardClicked)
            onDashboard()
        }
    )
}
