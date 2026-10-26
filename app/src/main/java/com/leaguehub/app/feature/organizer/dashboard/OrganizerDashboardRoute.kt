package com.leaguehub.app.feature.organizer.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.leaguehub.app.feature.organizer.components.OrganizerSection

@Composable
fun OrganizerDashboardRoute(
    onOpenControl: (String) -> Unit,
    onScan: (String) -> Unit,
    onManageMatch: (String) -> Unit,
    onSectionSelected: (OrganizerSection) -> Unit,
    viewModel: OrganizerDashboardViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    OrganizerDashboardScreen(
        leagueName = state.leagueName,
        match = state.match,
        summary = state.summary,
        pendingReports = state.pendingReports,
        onOpenControl = { onOpenControl(state.match.id) },
        onScan = { onScan(state.match.id) },
        onReportSelected = {
            viewModel.onEvent(OrganizerDashboardEvent.ReportSelected(it))
            onManageMatch(it)
        },
        onSectionSelected = onSectionSelected
    )
}
