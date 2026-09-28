package com.leaguehub.app.feature.organizer.dashboard

import androidx.compose.runtime.Composable
import com.leaguehub.app.data.fake.*

@Composable
fun OrganizerDashboardRoute() {
    OrganizerDashboardScreen(universityLeague.name, falconsVsTitans, fakeMatchdaySummary, fakePendingReports)
}
