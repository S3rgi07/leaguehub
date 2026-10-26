package com.leaguehub.app.feature.organizer.matchmanagement

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.leaguehub.app.LeagueHubApplication
import com.leaguehub.app.feature.organizer.components.OrganizerSection

@Composable
fun MatchManagementRoute(
    matchId: String,
    onStartLive: (String) -> Unit,
    onScan: (String) -> Unit,
    onSectionSelected: (OrganizerSection) -> Unit
) {
    val application = LocalContext.current.applicationContext as LeagueHubApplication
    val viewModel: MatchManagementViewModel = viewModel(
        key = "management-$matchId",
        factory = viewModelFactory {
            initializer { MatchManagementViewModel(application.matchWorkflowRepository) }
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    MatchManagementScreen(
        data = state.data,
        errorMessage = state.errorMessage,
        onStartLive = {
            viewModel.onEvent(MatchManagementEvent.StartLiveClicked)
            if (state.data.rosters.none { it.missing > 0 }) onStartLive(matchId)
        },
        onScan = { onScan(matchId) },
        onRosterSelected = { viewModel.onEvent(MatchManagementEvent.RosterSelected(it)) },
        onSectionSelected = onSectionSelected
    )
}
