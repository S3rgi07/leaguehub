package com.leaguehub.app.feature.organizer.dashboard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OrganizerDashboardViewModel : ViewModel() {
    private val _state = MutableStateFlow(OrganizerDashboardState())
    val state: StateFlow<OrganizerDashboardState> = _state.asStateFlow()

    fun onEvent(event: OrganizerDashboardEvent) {
        when (event) {
            is OrganizerDashboardEvent.ReportSelected ->
                _state.update { it.copy(selectedReportId = event.reportId) }
        }
    }
}
