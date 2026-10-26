package com.leaguehub.app.feature.organizer.dashboard

sealed interface OrganizerDashboardEvent {
    data class ReportSelected(val reportId: String) : OrganizerDashboardEvent
}
