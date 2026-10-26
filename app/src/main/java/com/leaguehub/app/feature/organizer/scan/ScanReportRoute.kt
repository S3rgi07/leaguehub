package com.leaguehub.app.feature.organizer.scan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.leaguehub.app.LeagueHubApplication
import com.leaguehub.app.feature.organizer.components.OrganizerSection

@Composable
fun ScanReportRoute(
    matchId: String,
    onProcessed: (String) -> Unit,
    onSectionSelected: (OrganizerSection) -> Unit
) {
    val application = LocalContext.current.applicationContext as LeagueHubApplication
    val viewModel: ScanReportViewModel = viewModel(
        key = "scan-$matchId",
        factory = viewModelFactory {
            initializer { ScanReportViewModel(application.matchWorkflowRepository) }
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.isProcessed) {
        if (state.isProcessed) onProcessed(matchId)
    }
    ScanReportScreen(
        match = state.match,
        status = state.status,
        homeScore = state.homeScore,
        awayScore = state.awayScore,
        onHomeScoreChange = { viewModel.onEvent(ScanReportEvent.HomeScoreChanged(it)) },
        onAwayScoreChange = { viewModel.onEvent(ScanReportEvent.AwayScoreChanged(it)) },
        onProcess = { viewModel.onEvent(ScanReportEvent.ProcessClicked) },
        onRetry = { viewModel.onEvent(ScanReportEvent.RetryClicked) },
        onSectionSelected = onSectionSelected
    )
}
